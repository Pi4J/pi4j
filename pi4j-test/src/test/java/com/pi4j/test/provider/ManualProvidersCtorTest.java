package com.pi4j.test.provider;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.test.Slf4jStreamBridge;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(Lifecycle.PER_CLASS)
public class ManualProvidersCtorTest {
    private static final Logger logger = LoggerFactory.getLogger(ManualProvidersCtorTest.class);
    private Context pi4j;

    @BeforeAll
    public void beforeTest() {
        // Initialize Pi4J with a manually configured context
        // ...
        // Explicitly add the test providers into the
        // context for testing
        pi4j = Pi4J.newContextBuilder().add(
            TestPwmProvider.newInstance(),
            TestI2CProvider.newInstance()
        ).build();
    }

    @AfterAll
    public void afterTest() {
        try {
            if (this.pi4j != null)
                pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testProvidersNotNull() {
        // ensure that the io collection in the Pi4J context is not NULL
        assertNotNull(pi4j.providers());
    }

    @Test
    public void testProviderCount() {
        // ensure that only 3 providers were detected/loaded into the Pi4J context
        assertEquals(2, pi4j.providers().all().size());
        var ps = Slf4jStreamBridge.createPrintStream(logger);
        // print out the detected Pi4J providers
        pi4j.providers().describe().print(ps);
    }
}
