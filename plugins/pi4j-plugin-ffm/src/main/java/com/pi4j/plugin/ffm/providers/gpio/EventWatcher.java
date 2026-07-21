package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.gpio.DetectedEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Runnable that blocks in a native {@code poll()} on the requested GPIO line file descriptor,
 * reads {@code struct gpio_v2_line_event} records as they arrive, applies optional software
 * debounce, and hands the resulting {@link DetectedEvent} list to a {@link PinEventProcessing}
 * callback. It runs until {@link #stopWatching()} is called.
 */
class EventWatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

    private final PollNative poll = new PollNative();

    // Graceful period for event watcher to shut down.
    // NOTE: used for poll() inside watcher thread — always keep at half of the total timeout.
    static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    private final int fd;
    private final int offset;
    private final FileDescriptorNative file;
    private final PinEvent pinEvent;
    private final PinEventProcessing eventProcessor;
    private final long debounceNs;

    private volatile boolean stopWatching = false;

    EventWatcher(int fd, int offset, long debounce, FileDescriptorNative file, PinEvent pinEvent, PinEventProcessing eventProcessor) {
        this.fd = fd;
        this.offset = offset;
        this.file = file;
        this.pinEvent = pinEvent;
        this.eventProcessor = eventProcessor;
        // convert microseconds to nanoseconds for software debounce
        this.debounceNs = debounce * 1000L;
    }

    @Override
    public void run() {
        var eventSize = (int) LineEvent.LAYOUT.byteSize();
        var timestamp = Instant.now();
        List<DetectedEvent> eventList = new ArrayList<>();
        DetectedEvent lastDebouncedEvent = null;
        PinEvent lastDebouncedState = null;
        long lastEventReceivedTimeNs = 0;
        logger.trace("{} - Start polling GPIO data on offset {} at {}",
            Thread.currentThread().getName(), offset, timestamp);
        while (!stopWatching) {
            try {
                // PollingData must be recreated each time — poll does not erase revents between calls
                var pollData = new PollingData(fd, (short) (PollFlag.POLLIN | PollFlag.POLLPRI), (short) 0);
                pollData = poll.poll(pollData, 1, EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2);
                if (pollData == null) {
                    var duration = timestamp.until(Instant.now()).toMillis();
                    logger.trace("{} - No events detected on offset {}: polling timeout at {} (took {}ms)",
                        Thread.currentThread().getName(), offset, timestamp, duration);
                    if (lastDebouncedEvent != null && debounceNs > 0 && lastEventReceivedTimeNs > 0) {
                        long currentTimeNs = System.nanoTime();
                        long timeSinceLastEventNs = currentTimeNs - lastEventReceivedTimeNs;
                        if (timeSinceLastEventNs >= debounceNs) {
                            logger.trace(
                                "{} - Dispatching pending debounced event on offset {} after timeout ({}ns >= {}ns)",
                                Thread.currentThread().getName(), offset, timeSinceLastEventNs, debounceNs);
                            eventList.add(lastDebouncedEvent);
                            eventProcessor.process(eventList);
                            eventList.clear();
                            lastDebouncedEvent = null;
                            lastEventReceivedTimeNs = 0;
                        }
                    }
                    timestamp = Instant.now();
                    continue;
                }
                if ((pollData.revents() & (PollFlag.POLLERR | PollFlag.POLLHUP | PollFlag.POLLNVAL)) != 0) {
                    logger.error("{} - Internal error during polling on offset {}. Last polling data: {}",
                        Thread.currentThread().getName(), offset, pollData);
                    stopWatching();
                    continue;
                }
                if ((pollData.revents() & (PollFlag.POLLIN | PollFlag.POLLPRI)) != 0) {
                    // default minimum buffer size is 16 line events
                    // see https://elixir.bootlin.com/linux/latest/source/include/uapi/linux/gpio.h#L185
                    var buf = file.read(fd, new byte[16 * eventSize], 16 * eventSize);
                    var holder = new byte[eventSize];
                    for (int i = 0; i < 16 * LineEvent.LAYOUT.byteSize(); i += eventSize) {
                        // Check all 8 bytes of timestamp_ns — checking only buf[i] (the LSB) is
                        // insufficient because a valid timestamp divisible by 256 ns has a zero LSB.
                        if ((buf[i] | buf[i + 1] | buf[i + 2] | buf[i + 3]
                            | buf[i + 4] | buf[i + 5] | buf[i + 6] | buf[i + 7]) == 0) {
                            continue;
                        }
                        System.arraycopy(buf, i, holder, 0, eventSize);
                        var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
                        memoryBuffer.asByteBuffer().put(holder);
                        var event = LineEvent.createEmpty().from(memoryBuffer);
                        logger.trace("{} - Detected new event on offset {}: {}",
                            Thread.currentThread().getName(), offset, event);
                        if ((event.id() & this.pinEvent.getValue()) != 0) {
                            var pinEventType = PinEvent.getByValue(event.id());
                            logger.trace("{} - Processing event on offset {}: {}",
                                Thread.currentThread().getName(), offset, pinEventType);
                            DetectedEvent detectedEvent =
                                new DetectedEvent(event.timestampNs(), pinEventType, event.lineSeqno());
                            if (debounceNs > 0) {
                                if (lastDebouncedEvent == null) {
                                    logger.trace("{} - Starting debounce period on offset {} for {}",
                                        Thread.currentThread().getName(), offset, pinEventType);
                                } else {
                                    long timeSinceLastEventNs =
                                        detectedEvent.timestampInNanos() - lastDebouncedEvent.timestampInNanos();
                                    if (timeSinceLastEventNs < debounceNs) {
                                        logger.trace(
                                            "{} - Event on offset {} within debounce period ({}ns < {}ns), updating to latest",
                                            Thread.currentThread().getName(), offset,
                                            timeSinceLastEventNs, debounceNs);
                                    } else {
                                        logger.trace(
                                            "{} - Debounce period passed on offset {} ({}ns >= {}ns), dispatching event",
                                            Thread.currentThread().getName(), offset,
                                            timeSinceLastEventNs, debounceNs);
                                        if (lastDebouncedState != null) {
                                            eventList.add(lastDebouncedEvent);
                                        }
                                    }
                                }
                                lastDebouncedEvent = detectedEvent;
                                lastDebouncedState = pinEventType;
                                lastEventReceivedTimeNs = System.nanoTime();
                            } else {
                                eventList.add(detectedEvent);
                            }
                        }
                    }
                    logger.trace("{} - Total events on offset {}: {}",
                        Thread.currentThread().getName(), offset, eventList.size());
                    if (!eventList.isEmpty()) {
                        eventProcessor.process(eventList);
                        eventList.clear();
                    }
                    logger.trace("{} - Total processing on offset {} took {}ms",
                        Thread.currentThread().getName(), offset, timestamp.until(Instant.now()).toMillis());
                    timestamp = Instant.now();
                }
            } catch (Throwable e) {
                logger.error("{} - Error while polling pin on offset {}",
                    Thread.currentThread().getName(), offset, e);
                throw new Pi4JException(e);
            }
        }
    }

    public void stopWatching() {
        this.stopWatching = true;
    }

    public boolean isRunning() {
        return !this.stopWatching;
    }
}