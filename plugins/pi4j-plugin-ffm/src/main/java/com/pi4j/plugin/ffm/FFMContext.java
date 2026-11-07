package com.pi4j.plugin.ffm;

import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2C;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPort;
import com.pi4j.io.gpio.parallel.ParallelDigitalInput;
import com.pi4j.io.gpio.parallel.ParallelDigitalOutput;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwm;
import com.pi4j.plugin.ffm.providers.spi.FFMSpi;


public class FFMContext extends DefaultContext {

    public FFMContext() {
        this(ContextBuilder.newInstance().toConfig());
    }

    public FFMContext(ContextConfig config) {
        super(config);
    }

    /**
     * TODO: Providing parallel devices here as a lazy shortcut, only to demonstrate parallel-backed digital devices.
     *  A merge candidate would either allow this to be configured or be implemented in a separate context
     *  implementation
     */
    @Override
    public IO<?> createImpl(IOConfig config, IOType type) {
        FFMPermissionHelper.checkUserPermissions(type);
        return switch (type) {
            case PWM -> FFMPwm.create((PwmConfig) config, this::shutdown);
            case I2C -> FFMI2C.create((I2CConfig) config, this::shutdown);
            case SPI -> new FFMSpi((SpiConfig) config, this::shutdown);
            case DIGITAL_INPUT -> {
                var parallelConfig = ParallelPortConfig.of((DigitalInputConfig) config);
                // don't need to shutdown the parallel port.
                // this is called by the digital device
                var parallelPort = FFMParallelPort.create(parallelConfig, _ -> {});
                yield new ParallelDigitalInput((DigitalInputConfig) config, parallelPort, this::shutdown);
            }
            case DIGITAL_OUTPUT -> {
                var parallelConfig = ParallelPortConfig.of((DigitalOutputConfig) config);
                // don't need to shutdown the parallel port.
                // this is called by the digital device
                var parallelPort = FFMParallelPort.create(parallelConfig, _ -> {});
                yield new ParallelDigitalOutput((DigitalOutputConfig) config, parallelPort, this::shutdown);
            }
            case PARALLEL -> FFMParallelPort.create((ParallelPortConfig) config, this::shutdown);
        };
    }

}
