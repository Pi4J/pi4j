package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock.FileDescriptorTestData;
import com.pi4j.plugin.ffm.mocks.PollNativeMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;

import java.lang.foreign.Arena;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Disabled because it is a performance profiling test, not a unit test. " +
    "Run manually when profiling is needed. "  +
    "Don't burn CPU cycles on CI/CD pipelines for this test.")
class EventWatcherTest {

    static {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    }

    private final Logger logger = LoggerFactory.getLogger(EventWatcherTest.class);

    /**
     * Fake polling data that indicates the poller should stop watching for events.
     * This is used to simulate a stop condition in the test.
     */
    private final PollingData stopPollingData = new PollingData(1, (short) 0, (short) PollFlag.POLLNVAL);

    /**
     * Fake polling data that indicates the poller should keep watching for events.
     * This is used to simulate a continue condition in the test.
     */
    private final PollingData keepPollingData = new PollingData(1, (short) 0, (short) PollFlag.POLLIN);

    /**
     * Fake line events to simulate GPIO events for testing.
     * Each event is represented as a byte array corresponding to the serialized form of a {@link LineEvent}.
     * The events are pre-generated so their seriailisation has a minimal effect on profile results.
     */
    private final byte[][] fakeLineEvents;

    public EventWatcherTest() {
        fakeLineEvents = new byte[1_000][];
        SegmentAllocator allocator = Arena.ofAuto();
        for (int i = 0; i < fakeLineEvents.length; i++) {
            try {
                var event = new LineEvent(i + 1, 1, 2, i, i);
                var segment = allocator.allocate(LineEvent.LAYOUT);
                event.to(segment);
                fakeLineEvents[i] = segment.toArray(ValueLayout.JAVA_BYTE);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    /**
     * Execute 10,000,000 polling iterations for profiling.
     * <p>
     * Polling and file operations are mocked with Mockito via existing test fixtures
     */
    @Test
    void profileWithMockito() {
        AtomicInteger outCounter = new AtomicInteger();
        AtomicInteger inCounter = new AtomicInteger();

        var pollerNative = PollNativeMock.setup(_ -> (outCounter.get() >= 5_000_000)
                ? stopPollingData
                : keepPollingData
        );

        var fileDescriptorNative = FileDescriptorNativeMock.setup(new FileDescriptorTestData("blah", 1, new byte[0], invocationOnMock -> {
            int index = outCounter.getAndIncrement() % fakeLineEvents.length;
            var bytes = fakeLineEvents[index];
            System.arraycopy(bytes, 0, (byte[]) invocationOnMock.getArgument(1), 0, bytes.length);
            return invocationOnMock.getArgument(1);
        }));

        PinEventProcessing eventProcessor = (lineEventList) -> {
            assertEquals(1, lineEventList.size());
            var event = lineEventList.getFirst();
            assertEquals((outCounter.get() - 1)  % fakeLineEvents.length, event.seqno());
            inCounter.getAndAdd(lineEventList.size());
        };

        var eventWatcher = new EventWatcher(1, 1, PinEvent.BOTH, eventProcessor);
        eventWatcher.run();
        logger.info("Event watcher finished after {} polls.", inCounter.get());

        // If we have other tests which depend on poller or file descriptor, the mocks for this test need to be disposed
        // to ensure they don't interfere with other tests.
        fileDescriptorNative.close();
        pollerNative.close();
    }

    /**
     * Execute 10,000,000 polling iterations for profiling.
     * <p>
     * Polling and file operations are faked with local lambda implementations
     */
    @Test
    void profileWithFakes() {
        AtomicInteger outCounter = new AtomicInteger();
        AtomicInteger inCounter = new AtomicInteger();

        PinEventProcessing eventProcessor = (lineEventList) -> {
            assertEquals(1, lineEventList.size());
            var event = lineEventList.getFirst();
            assertEquals((outCounter.get() - 1)  % fakeLineEvents.length, event.seqno());
            inCounter.getAndAdd(lineEventList.size());
        };

        EventWatcher.EventPoller poller = (_, _, _) -> outCounter.get() >= 5_000_000
            ? stopPollingData
            : keepPollingData;

        EventWatcher.EventReader reader = (_, buffer, _) -> {
            int index = outCounter.getAndIncrement() % fakeLineEvents.length;
            var bytes = fakeLineEvents[index];
            System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            return buffer;
        };

        var eventWatcher = new EventWatcher(1, 1, PinEvent.BOTH, eventProcessor, poller, reader);
        eventWatcher.run();
        logger.info("Event watcher finished after {} polls.", inCounter.get());
    }
}