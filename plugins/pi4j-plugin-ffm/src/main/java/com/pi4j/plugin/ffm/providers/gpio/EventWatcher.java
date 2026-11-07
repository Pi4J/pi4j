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
import java.lang.foreign.MemorySegment;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Runnable that blocks in a native {@code poll()} on the requested GPIO line file descriptor,
 * reads {@code struct gpio_v2_line_event} records as they arrive, applies optional software
 * debounce, and hands the resulting {@link LineEvent} list to a {@link PinEventProcessing}
 * callback. It runs until {@link #stopWatching()} is called.
 */
public class EventWatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

    // Graceful period for event watcher to shut down.
    // NOTE: used for poll() inside watcher thread — always keep at half of the total timeout.
    static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    private final long mask;
    private final PinEvent pinEvent;
    private final PinEventProcessing eventProcessor;

    private final EventPoller eventPoller;
    private final EventReader eventReader;

    private volatile boolean stopWatching = false;
    /**
     * {@link BlockingQueue} which is polled after {@link #stopWatching} is set. This allows a stop request
     * to wait until the last polling loop is complete before returning.
     */
    private final BlockingQueue<Boolean> shutdownCompleteSignal = new ArrayBlockingQueue<>(1);

    /**
     * @param fd file descriptor of the GPIO line
     * @param mask bitmask of the GPIO line
     * @param pinEvent the pin even type to capture (RISING, FALLING, BOTH)
     * @param eventProcessor callback to process the events
     */
    public EventWatcher(
        int fd,
        long mask,
        PinEvent pinEvent,
        PinEventProcessing eventProcessor
    ) {
        this(
            mask,
            pinEvent,
            eventProcessor,
            new DefaultEventPoller(fd),
            /*
             default minimum buffer size is 16 line events
             see https://elixir.bootlin.com/linux/latest/source/include/uapi/linux/gpio.h#L185
             */
            new DefaultEventReader(fd, (int) LineEvent.LAYOUT.byteSize() * 16)
        );
    }

    /**
     * Package private constructor for testing purposes, allowing injection of custom poller and reader implementations.
     * @param mask bitmask of the GPIO line
     * @param pinEvent the pin even type to capture (RISING, FALLING, BOTH)
     * @param eventProcessor callback to process the events
     * @param eventPoller context-appropriate {@link EventPoller}
     * @param eventReader context-appropriate {@link EventReader}
     */
    EventWatcher(
        long mask,
        PinEvent pinEvent,
        PinEventProcessing eventProcessor,
        EventPoller eventPoller,
        EventReader eventReader
    ) {
        this.mask = mask;
        this.pinEvent = pinEvent;
        this.eventProcessor = eventProcessor;
        this.eventPoller = eventPoller;
        this.eventReader = eventReader;
    }

    @Override
    public void run() {
        var eventSize = (int) LineEvent.LAYOUT.byteSize();
        var bufferSize = eventSize * 16; // default minimum buffer size is 16 line events
        var timestamp = Instant.now();
        List<LineEvent> eventList = new ArrayList<>();

        if (logger.isTraceEnabled()) {
            logger.trace("Start polling GPIO data on offset {} at {}", mask, timestamp);
        }

        // clear non-consumed shutdown notifications
        shutdownCompleteSignal.clear();
        // clear the stop watching flag
        stopWatching = false;

        try (var arena = Arena.ofConfined()) {
            // Allocate a buffer large enough to hold multiple events.
            // We're only going to read as many bytes from this as we are told have been read,
            // so it's safe to over-allocate here.
            var memoryBuffer = arena.allocate(bufferSize);

            while (!stopWatching) {
                try {
                    /*
                     EventWatcher is not interested in how the PollingData is constructed, only that it is returned
                     from the poll() call.
                     */
                    var pollData = eventPoller.poll();
                    if (stopWatching) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Watcher was requested to stop while waiting for poll to complete");
                        }
                        // flush the event buffer
                        if (pollData != null && (pollData.revents() & (PollFlag.POLLIN | PollFlag.POLLPRI)) != 0) {
                            var bufferLength = eventReader.read(memoryBuffer);
                            if (logger.isTraceEnabled()) {
                                logger.trace("Flushed {} events from buffer", bufferLength / eventSize);
                            }
                        }
                        break;
                    }
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
                        /*
                         Again, EventWatcher is not interested in how the event data is fetched, only that it can be
                          called to read events and return the number of bytes available.
                         */
                        var bytesAvailable = eventReader.read(memoryBuffer);
                        for (int i = 0; i < bytesAvailable; i += eventSize) {
                            var slice = memoryBuffer.asSlice(i, eventSize);

                            var event = LineEvent.createEmpty().from(slice);
                            if (event.timestampNs() == 0L) {
                                continue;
                            }

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
                            timestamp = Instant.now();
                        }
                    }
                } catch (Throwable e) {
                    logger.error("Error while polling pin on offset {}", mask, e);
                    throw new Pi4JException(e);
                }
            }
        }

        // emit shutdown notification
        shutdownCompleteSignal.add(true);
    }

    public void stopWatching() {
        this.stopWatching = true;
        try {
            // wait up to 100ms for shutdown complete notification
            shutdownCompleteSignal.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return !this.stopWatching;
    }

    /**
     * Local abstraction of the polling operation to allow for testing and mocking of the native poll() call.
     * The default implementation uses {@link PollNative#poll(PollingData, int, int)}
     */
    @FunctionalInterface
    interface EventPoller {
        PollingData poll();
    }

    private static class DefaultEventPoller implements EventPoller {
        private final PollNative pollNative = new PollNative();
        private final int fileDescriptor;

        public DefaultEventPoller(int fileDescriptor) {
            this.fileDescriptor = fileDescriptor;
        }

        @Override
        public PollingData poll() {
            // PollingData must be recreated each time — poll does not erase revents between calls
            var data = new PollingData(fileDescriptor, (short) (PollFlag.POLLIN | PollFlag.POLLPRI), (short) 0);
            return pollNative.poll(data, 1, EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2);
        }
    }

    /**
     * Local abstraction of the event reading operation to allow for testing and mocking of the native read() call.
     * The default implementation uses {@link FileDescriptorNative#read(int, byte[], int)}
     */
    @FunctionalInterface
    interface EventReader {
        int read(MemorySegment buffer);
    }

    private static class DefaultEventReader implements EventReader {
        private final FileDescriptorNative fileDescriptorNative = new FileDescriptorNative();
        private final int fileDescriptor;
        private final int bufferSize;

        public DefaultEventReader(int fileDescriptor, int bufferSize) {
            this.fileDescriptor = fileDescriptor;
            this.bufferSize = bufferSize;
        }

        @Override
        public int read(MemorySegment buffer) {
            return fileDescriptorNative.read(fileDescriptor, buffer, bufferSize);
        }
    }
}