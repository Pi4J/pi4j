package com.pi4j.plugin.mock;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProviderImpl;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProviderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for runtime pin reconfiguration ({@link com.pi4j.io.gpio.digital.PinReconfigurer})
 * exercised against the in-memory mock digital drivers. The reconfigure mechanism itself lives in
 * pi4j-core ({@code DefaultPinReconfigurer}) and is provider-agnostic, so driving it through the mock
 * providers verifies the full create → shutdown → recreate flow without real hardware.
 */
class ReconfigureTest {

    private final Context pi4j = Pi4J.newContextBuilder()
        .add(new MockDigitalInputProviderImpl(), new MockDigitalOutputProviderImpl())
        .build();

    @AfterEach
    void tearDown() {
        if (!pi4j.isShutdown()) {
            pi4j.shutdown();
        }
    }

    @Test
    void reconfiguresInputToOutputWithExplicitConfig() throws Exception {
        var input = pi4j.digitalInput().create(
            DigitalInputConfigBuilder.newInstance().bcm(4).build());
        assertTrue(pi4j.registry().exists(input.id()));

        var output = input.reconfigure().digitalOutput().create(
            DigitalOutputConfigBuilder.newInstance().bcm(4).initial(DigitalState.HIGH).build());

        // old input released, new output registered on the same line
        assertFalse(pi4j.registry().exists(input.id()));
        assertEquals(4, output.bcm());
        assertEquals(DigitalState.HIGH, output.state());

        output.state(DigitalState.LOW);
        assertEquals(DigitalState.LOW, output.state());
    }

    @Test
    void reconfiguresOutputToInputWithExplicitConfig() throws Exception {
        var output = pi4j.digitalOutput().create(
            DigitalOutputConfigBuilder.newInstance().bcm(5).build());
        assertTrue(pi4j.registry().exists(output.id()));

        var input = output.reconfigure().digitalInput().create(
            DigitalInputConfigBuilder.newInstance().bcm(5).pull(PullResistance.PULL_UP).build());

        assertFalse(pi4j.registry().exists(output.id()));
        assertEquals(5, input.bcm());
        assertEquals(PullResistance.PULL_UP, input.pull());
    }

    @Test
    void reconfiguresInputToOutputReusingConfig() throws Exception {
        var input = pi4j.digitalInput().create(
            DigitalInputConfigBuilder.newInstance().bcm(6).bus(1).build());

        // no explicit builder — the current pin's BCM/bus addressing is reused
        var output = input.reconfigure().digitalOutput().create();

        assertFalse(pi4j.registry().exists(input.id()));
        assertEquals(6, output.bcm());
        assertEquals(input.config().bus(), output.config().bus());
        assertTrue(pi4j.registry().exists(output.id()));
    }

    @Test
    void reconfiguresOutputToInputReusingConfig() throws Exception {
        var output = pi4j.digitalOutput().create(
            DigitalOutputConfigBuilder.newInstance().bcm(7).bus(2).build());

        var input = output.reconfigure().digitalInput().create();

        assertFalse(pi4j.registry().exists(output.id()));
        assertEquals(7, input.bcm());
        assertEquals(output.config().bus(), input.config().bus());
        assertTrue(pi4j.registry().exists(input.id()));
    }

    @Test
    void canDriveReconfiguredLineAfterMultipleReconfigurations() throws Exception {
        // input -> output -> input -> output on the same BCM line
        var pin = pi4j.digitalInput().create(
            DigitalInputConfigBuilder.newInstance().bcm(8).build());

        var asOutput = pin.reconfigure().digitalOutput().create();
        asOutput.state(DigitalState.HIGH);
        assertEquals(DigitalState.HIGH, asOutput.state());

        var asInput = asOutput.reconfigure().digitalInput().create();
        assertEquals(8, asInput.bcm());

        var asOutputAgain = asInput.reconfigure().digitalOutput().create();
        asOutputAgain.state(DigitalState.HIGH);
        assertEquals(DigitalState.HIGH, asOutputAgain.state());
        assertTrue(pi4j.registry().exists(asOutputAgain.id()));
    }
}
