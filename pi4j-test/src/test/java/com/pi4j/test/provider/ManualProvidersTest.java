package com.pi4j.test.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.test.Slf4jStreamBridge;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualProvidersTest {

    private static final Logger logger = LoggerFactory.getLogger(ManualProvidersTest.class);

    @Test
    public void testProviders() {

        // create our own custom provider implementation classes
        PwmProvider pwmProvider = TestPwmProvider.newInstance();
        I2CProvider i2CProvider = TestI2CProvider.newInstance();

        // Initialize Pi4J with an empty context
        // An empty context disables AUTO-DETECT loading
        // which will not load any detected Pi4J extension
        // libraries (Platforms and Providers) from the class path
        // ...
        // add the custom providers to the Pi4J context
        Context pi4j = Pi4J.newContextBuilder().add(pwmProvider, i2CProvider).build();

        // ensure that the io collection in the Pi4J context is not NULL
        assertNotNull(pi4j.providers());

        // ensure that no io were detected/loaded into the Pi4J context
        assertEquals(2, pi4j.providers().all().size());

        // print out the detected Pi4J io libraries found on the class path
        logger.info("2 CUSTOM PROVIDERS (added via API)");
        var ps = Slf4jStreamBridge.createPrintStream(logger);
        pi4j.providers().describe().print(ps);

        // shutdown Pi4J runtime
        pi4j.shutdown();
    }
}
