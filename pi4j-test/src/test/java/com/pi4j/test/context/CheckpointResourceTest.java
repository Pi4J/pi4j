package com.pi4j.test.context;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.context.impl.CheckpointResource;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.RestoreException;
import com.pi4j.io.Checkpointable;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Drives the CRaC {@link CheckpointResource} directly against a mock context, as a real checkpoint needs a
 * CRaC-enabled JVM on Linux.
 */
class CheckpointResourceTest {

    private Context pi4j;
    private CheckpointResource resource;
    private List<String> calls;

    @BeforeEach
    void beforeTest() {
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().build();
        resource = ((DefaultContext) pi4j).checkpointResource();
        calls = new ArrayList<>();
    }

    @AfterEach
    void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException _) { /* do nothing */ }
    }

    @Test
    void testSuspendsInReverseAndResumesInCreationOrder() throws Exception {
        var first = register("first", 20);
        // a mock IO that doesn't implement Checkpointable must be skipped
        pi4j.create(DigitalOutput.newConfigBuilder(pi4j).id("plain").bcm(21).build());
        var second = register("second", 22);

        resource.beforeCheckpoint(null);
        assertTrue(first.suspended);
        assertTrue(second.suspended);

        resource.afterRestore(null);
        assertFalse(first.suspended);
        assertFalse(second.suspended);

        assertEquals(List.of("suspend second", "suspend first", "resume first", "resume second"), calls);
    }

    @Test
    void testRestoreFailuresAreCollected() throws Exception {
        var first = register("first", 20);
        var second = register("second", 22);
        var third = register("third", 23);
        first.failResume = true;
        third.failResume = true;

        resource.beforeCheckpoint(null);
        var e = assertThrows(RestoreException.class, () -> resource.afterRestore(null));

        // every instance is attempted, even after a failure
        assertFalse(second.suspended);
        assertEquals(2, e.getSuppressed().length);
        assertTrue(e.getSuppressed()[0].getMessage().contains("'first'"));
        assertTrue(e.getSuppressed()[1].getMessage().contains("'third'"));
    }

    @Test
    void testSingleRestoreFailureNamesTheIO() throws Exception {
        var first = register("first", 20);
        first.failResume = true;

        resource.beforeCheckpoint(null);
        var e = assertThrows(RestoreException.class, () -> resource.afterRestore(null));
        assertTrue(e.getMessage().contains("'first'"));
    }

    @Test
    void testSuspendFailureResumesAlreadySuspended() {
        var first = register("first", 20);
        var second = register("second", 22);
        first.failSuspend = true;

        var e = assertThrows(LifecycleException.class, () -> resource.beforeCheckpoint(null));
        assertTrue(e.getMessage().contains("'first'"));
        assertFalse(second.suspended);
        assertEquals(List.of("suspend second", "resume second"), calls);
    }

    @Test
    void testBoardInfoRedetectedAfterRestore() throws Exception {
        var before = pi4j.boardInfo();
        resource.beforeCheckpoint(null);
        resource.afterRestore(null);
        assertNotNull(pi4j.boardInfo());
        assertNotSame(before, pi4j.boardInfo());
    }

    @Test
    void testNoOpAfterShutdown() throws Exception {
        var first = register("first", 20);
        pi4j.shutdown();

        resource.beforeCheckpoint(null);
        resource.afterRestore(null);
        assertFalse(first.suspended);
        assertTrue(calls.isEmpty());
    }

    private RecordingOutput register(String id, int bcm) {
        var io = new RecordingOutput(pi4j, DigitalOutput.newConfigBuilder(pi4j).id(id).bcm(bcm).build(), calls);
        pi4j.register(io);
        return io;
    }

    private static class RecordingOutput extends DigitalOutputBase implements Checkpointable {
        private final List<String> calls;
        boolean suspended = false;
        boolean failSuspend = false;
        boolean failResume = false;

        RecordingOutput(Context context, DigitalOutputConfig config, List<String> calls) {
            super(context, config);
            this.calls = calls;
        }

        @Override
        public void suspend() {
            if (failSuspend) {
                throw new Pi4JException("suspend failed");
            }
            calls.add("suspend " + id());
            suspended = true;
        }

        @Override
        public void resume() {
            if (failResume) {
                throw new Pi4JException("resume failed");
            }
            calls.add("resume " + id());
            suspended = false;
        }
    }
}
