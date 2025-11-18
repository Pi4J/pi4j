package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.ffm.common.PermissionHelper;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.util.DeferredDelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PwmFFMHardware extends PwmBase implements Pwm {
    private Logger logger = LoggerFactory.getLogger(PwmFFMHardware.class);

    private final FileDescriptorNative file = new FileDescriptorNative();

    private static final String CHIP_PATH = "/sys/class/pwm/pwmchip";
    private static final String CHIP_EXPORT_PATH = "/export";
    private static final String CHIP_UNEXPORT_PATH = "/unexport";
    private static final String CHIP_NPWM_PATH = "/npwm";
    private static final String PWM_PATH = "/pwm";
    private static final String ENABLE_PATH = "/enable";
    private static final String DUTY_CYCLE_PATH = "/duty_cycle";
    private static final String PERIOD_PATH = "/period";
    private static final String POLARITY_PATH = "/polarity";

    /**
     * Since the real size of files in sysfs cannot be determined until read,
     * we assume the max value for the files in PWM is int32 (2 ^ 32).
     * Files are text ASCII (not binary), so the max int in string representation is 10 bytes.
     */
    private static final int MAX_FILE_SIZE = 10;
    private static final long NANOS_IN_SECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);

    private String pwmPath;
    private final int chip;
    private final int channel;

    public PwmFFMHardware(PwmProvider provider, PwmConfig config) {
        super(provider, config);
        this.chip = config.chip();
        this.channel = config.channel();
        PermissionHelper.checkDevicePermissions(CHIP_PATH + chip, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        var pwmChipFile = CHIP_PATH + chip;

        var pwmFile = pwmChipFile + PWM_PATH + channel;
        if (deviceNotExists(pwmFile)) {
            logger.trace("{} - no PWM Bus found... will try to export PWM Bus first.", pwmFile);
            var npwmFd = file.open(pwmChipFile + CHIP_NPWM_PATH, FileFlag.O_RDONLY);
            var maxChannel = getIntegerContent(file.read(npwmFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(npwmFd);
            if (chip > maxChannel - 1) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile + "' cannot be exported! Max available channel is " + maxChannel);
            }
            var exportFd = file.open(pwmChipFile + CHIP_EXPORT_PATH, FileFlag.O_WRONLY);
            file.write(exportFd, getByteContent(chip));
            file.close(exportFd);
            if (deviceNotExists(pwmFile)) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile + "' cannot be exported!");
            }
        }
        this.pwmPath = pwmFile;

        waitForPermissions(this.pwmPath + ENABLE_PATH, 0);
        var stateFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDONLY);
        this.onState = getIntegerContent(file.read(stateFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)) == 1;
        file.close(stateFd);

        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            var dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_RDONLY);
            this.dutyCycle = getIntegerContent(file.read(dutyCycleFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(dutyCycleFd);
        }

        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            var polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_RDONLY);
            this.polarity = PwmPolarity.parse(getStringContent(file.read(polarityFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)));
            file.close(polarityFd);
        }

        if (config.frequency() != null) {
            this.frequency = config.frequency();
            this.period = NANOS_IN_SECOND / this.frequency;
        } else {
            var periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDONLY);
            this.period = getIntegerContent(file.read(periodFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(periodFd);
        }

        logger.debug("{} - pwm setup finished. Initial state: {}", pwmPath, this.onState ? "on" : "off");
        return this;
    }

    @Override
    public Pwm on() throws IOException {
        if (onState) {
            logger.warn("{} - PWM Bus is already enabled.", pwmPath);
            return this;
        }
        if (frequency < 0) {
            logger.error("{} - cannot set frequency '{}', required more then 0.", pwmPath, frequency);
            throw new Pi4JException("cannot set frequency '" + frequency + "', required more then 0.");
        }
        this.period = (NANOS_IN_SECOND / frequency);
        logger.debug("{} - period is '{}', dutyCycle is '{}' and polarity '{}'.", pwmPath, period, dutyCycle, polarity);

        var periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_WRONLY);
        var dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_WRONLY);
        var polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_WRONLY);

        file.write(periodFd, String.valueOf(period).getBytes());

        var dCycle = Math.round((double) (period * dutyCycle) / 100);
        file.write(dutyCycleFd, String.valueOf(dCycle).getBytes());

        file.write(polarityFd, polarity.getName().getBytes());

        file.close(dutyCycleFd);
        file.close(periodFd);
        file.close(polarityFd);

        var enableFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        file.write(enableFd, String.valueOf(1).getBytes());
        file.close(enableFd);
        this.onState = true;
        return this;
    }

    @Override
    public Pwm off() throws IOException {
        if (!onState) {
            logger.warn("{} - PWM Bus is already disabled.", pwmPath);
            return this;
        }
        var enableFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        file.write(enableFd, String.valueOf(0).getBytes());
        file.close(enableFd);
        this.onState = false;
        return this;
    }

    @Override
    public Pwm shutdownInternal(Context context) throws ShutdownException {
        if (config.getShutdownValue() != null) {
            return super.shutdownInternal(context);
        }

        var exportFd = file.open(CHIP_PATH + chip + CHIP_UNEXPORT_PATH, FileFlag.O_WRONLY);
        file.write(exportFd, getByteContent(channel));
        file.close(exportFd);

        return this;
    }

    /**
     * Since read/write of file descriptors accepts only byte arrays / text, we have to convert inputs from text bytes to numbers.
     *
     * @param bytes text byte array to be converted
     * @return integer representation of text byte array
     */
    private static int getIntegerContent(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes cannot be null");
        }
        return Integer.parseInt(new String(bytes).trim());
    }

    private static String getStringContent(byte[] bytes) {
        return new String(bytes).trim();
    }

    private static byte[] getByteContent(int number) {
        return String.valueOf(number).getBytes();
    }

    private boolean deviceNotExists(String path) {
        var access = file.access(path, FileFlag.F_OK);
        logger.trace("{} - device has access flag '{}'", pwmPath, access);
        return access != 0;
    }

    /**
     * Waits the udev rules to be applied for 100ms at most.
     *
     * @param path    path of the file
     * @param timeout counting timeout
     */
    private void waitForPermissions(String path, int timeout) {
        if (timeout > 100) {
            throw new Pi4JException("Timeout occurred while waiting for permissions");
        }
        logger.trace("{} - Waiting for permissions '{}' for {}ms", pwmPath, path, timeout);
        var access = file.access(path, FileFlag.R_OK);
        if (access != 0) {
            var deferredDelay = new DeferredDelay();
            deferredDelay.setDelayMillis(10);
            deferredDelay.materializeDelay();
            waitForPermissions(path, timeout + 10);
        }
    }


}
