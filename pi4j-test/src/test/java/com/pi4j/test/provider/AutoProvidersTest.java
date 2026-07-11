package com.pi4j.test.provider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.test.Slf4jStreamBridge;
import com.pi4j.test.context.ContextTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class AutoProvidersTest {
    private static final Logger logger = LoggerFactory.getLogger(AutoProvidersTest.class);
    private Context pi4j;

    @BeforeAll
    public void beforeTest() {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectProviders().build();
    }

    @AfterAll
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testProvidersNotNull() {
        assertNotNull(pi4j.providers());
    }

    @Test
    public void testProvidersNotEmpty() {
        // ensure that 1 or more providers were detected/loaded into the Pi4J context
        assertFalse(pi4j.providers().all().isEmpty());
        var ps = Slf4jStreamBridge.createPrintStream(logger);
        // print out the detected Pi4J io libraries found on the class path
        pi4j.providers().describe().print(ps);
    }
}
