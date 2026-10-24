package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
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
import java.util.Collections;
import java.util.List;

/**
 * Runnable that blocks in a native {@code poll()} on the requested GPIO line file descriptor,
 * reads {@code struct gpio_v2_line_event} records as they arrive, applies optional software
 * debounce, and hands the resulting {@link LineEvent} list to a {@link PinEventProcessing}
 * callback. It runs until {@link #stopWatching()} is called.
 */
public class EventWatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

    private final PollNative poll = new PollNative();

    // Graceful period for event watcher to shut down.
    // NOTE: used for poll() inside watcher thread — always keep at half of the total timeout.
    static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    private final int fd;
    private final long mask;
    private final FileDescriptorNative file;
    private final PinEvent pinEvent;
    private final PinEventProcessing eventProcessor;

    private volatile boolean stopWatching = false;

    /**
     * @param fd file descriptor of the GPIO line
     * @param mask bitmask of the GPIO line
     * @param file file operations
     * @param pinEvent the pin even type to capture (RISING, FALLING, BOTH)
     * @param eventProcessor callback to process the events
     */
    public EventWatcher(int fd, long mask, FileDescriptorNative file, PinEvent pinEvent, PinEventProcessing eventProcessor) {
        this.fd = fd;
        this.mask = mask;
        this.file = file;
        this.pinEvent = pinEvent;
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void run() {
        var eventSize = (int) LineEvent.LAYOUT.byteSize();
        var timestamp = Instant.now();
        List<LineEvent> eventList = new ArrayList<>();

        if (logger.isTraceEnabled()) {
            logger.trace("Start polling GPIO data on offset {} at {}", mask, timestamp);
        }

        while (!stopWatching) {
            try {
                // PollingData must be recreated each time — poll does not erase revents between calls
                var pollData = new PollingData(fd, (short) (PollFlag.POLLIN | PollFlag.POLLPRI), (short) 0);
                pollData = poll.poll(pollData, 1, EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2);
                if (pollData == null) {
                    eventProcessor.process(Collections.emptyList());
                    continue;
                }
                if ((pollData.revents() & (PollFlag.POLLERR | PollFlag.POLLHUP | PollFlag.POLLNVAL)) != 0) {
                    logger.error("Internal error during polling on offset {}. Last polling data: {}", mask, pollData);
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

                        if (logger.isTraceEnabled()) {
                            logger.trace("Detected new event on offset {}: {}", mask, event);
                        }

                        if ((event.id() & this.pinEvent.getValue()) != 0) {
                            eventList.add(event);
                        }
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("Total events on offset {}: {}", mask, eventList.size());
                    }

                    if (!eventList.isEmpty()) {
                        eventProcessor.process(eventList);
                        eventList.clear();
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace("Total processing on offset {} took {}ms", mask, timestamp.until(Instant.now()).toMillis());
                    }

                    timestamp = Instant.now();
                }
            } catch (Throwable e) {
                logger.error("Error while polling pin on offset {}", mask, e);
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