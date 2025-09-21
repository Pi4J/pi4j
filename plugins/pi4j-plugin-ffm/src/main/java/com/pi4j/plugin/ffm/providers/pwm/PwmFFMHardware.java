package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
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
    private final int pwmBusNumber;
    private final int pwmChipNumber;

    public PwmFFMHardware(PwmProvider provider, PwmConfig config){
        super(provider, config);
        this.pwmBusNumber = config.busNumber();
        this.pwmChipNumber = config.address();
    }

    /** {@inheritDoc} */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        var pwmChipFile = CHIP_PATH + pwmChipNumber;
        if (!deviceExists(pwmChipFile)) {
            throw new IllegalArgumentException("PWM Chip at path '" + pwmChipFile + "' does not exist!");
        }
        var pwmFile = pwmChipFile + PWM_PATH + pwmBusNumber;
        if (!deviceExists(pwmFile)) {
            logger.trace("{} - no PWM Bus found... will try to export PWM Bus first.", pwmFile);
            var npwmFd = file.open(pwmChipFile + CHIP_NPWM_PATH, FileFlag.O_RDONLY);
            var maxChannel = getIntegerContent(file.read(npwmFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(npwmFd);
            if (pwmBusNumber > maxChannel - 1) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile + "' cannot be exported! Max available channel is " + maxChannel);
            }
            var exportFd = file.open(pwmChipFile + CHIP_EXPORT_PATH, FileFlag.O_WRONLY);
            file.write(exportFd, getByteContent(pwmBusNumber));
            file.close(exportFd);
            if (!deviceExists(pwmFile)) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile + "' cannot be exported!");
            }
        }
        this.pwmPath = pwmFile;

        var stateFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        this.onState = getIntegerContent(file.read(stateFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)) == 1;
        file.close(stateFd);

        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            var dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_RDWR);
            this.dutyCycle = getIntegerContent(file.read(dutyCycleFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(dutyCycleFd);
        }

        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            var polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_RDWR);
            this.polarity = PwmPolarity.parse(getStringContent(file.read(polarityFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)));
            file.close(polarityFd);
        }

        if (config.frequency() != null) {
            this.frequency = config.frequency();
            this.period = NANOS_IN_SECOND / this.frequency;
        } else {
            var periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDWR);
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
        file.write(dutyCycleFd, String.valueOf(dutyCycle).getBytes());
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
    public Pwm shutdown(Context context) throws ShutdownException {
        if (config.getShutdownValue() != null) {
            return super.shutdown(context);
        }

        var exportFd = file.open(CHIP_PATH + pwmChipNumber + CHIP_UNEXPORT_PATH, FileFlag.O_WRONLY);
        file.write(exportFd, getByteContent(pwmBusNumber));
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
        return Integer.parseInt(new String(bytes).trim());
    }

    private static String getStringContent(byte[] bytes) {
        return new String(bytes).trim();
    }

    private static byte[] getByteContent(int number) {
        return String.valueOf(number).getBytes();
    }

    private boolean deviceExists(String path) {
        var access = file.access(path, FileFlag.F_OK);
        logger.trace("{} - device has access flag '{}'", pwmPath, access);
        return access == 0;
    }


}
