package com.pi4j.test.context;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

@TestInstance(Lifecycle.PER_CLASS)
public class ContextTest {

    private static final Logger logger = LoggerFactory.getLogger(ContextTest.class);

    private Context pi4j;

    @BeforeAll
    public void beforeTest() throws Pi4JException {
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
    public void testFactoryContextAcquisition() throws Pi4JException {
        assertNotNull(pi4j);
        logger.info("-------------------------------------------------");
        logger.info("Pi4J CONTEXT <acquired via factory accessor>");
        logger.info("-------------------------------------------------");
        var ps = Slf4jStreamBridge.createPrintStream(logger);
        pi4j.describe().print(ps);
    }
}
