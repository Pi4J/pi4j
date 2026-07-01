package com.pi4j.test.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalInput;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class RegistryGetIoInstance {

    public static final int PIN_ADDRESS = 1;
    public static final String PIN_ID = "my-custom-pin";

    private Context pi4j;

    @BeforeAll
    public void beforeTest() throws Pi4JException {
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterAll
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testGetIoInstanceFromRegistry() {

        // create a simple I/O instance
        DigitalInput input = pi4j.din().create(PIN_ADDRESS, PIN_ID);

        // attempt to get I/O instance from registry
        DigitalInput retrieved = pi4j.registry().get(PIN_ID, DigitalInput.class);

        // verify the retrieved I/O instance is the same as the one we registered
        assertEquals(input,retrieved, "The I/O instance retrieved from registry is not a match.");
    }
}
