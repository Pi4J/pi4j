package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class PwmFFMHardware extends PwmBase implements Pwm {
    private Logger logger = LoggerFactory.getLogger(PwmFFMHardware.class);

    private final FileDescriptorNative FILE = new FileDescriptorNative();

    private static final String CHIP_PATH = "/sys/class/pwm/pwmchip";
    private static final String CHIP_EXPORT_PATH = "/export";
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
        var pwmChipFile = Path.of(CHIP_PATH + pwmChipNumber).toFile();
        if (!pwmChipFile.exists()) {
            throw new IllegalArgumentException("PWM Chip at path '" + pwmChipFile.getPath() + "' does not exist!");
        }
        var pwmFile = Path.of(pwmChipFile.getPath() + PWM_PATH + pwmBusNumber).toFile();
        if (!pwmFile.exists()) {
            logger.trace("{} - no PWM Bus found... will try to export PWM Bus first.", pwmFile);
            var npwmFd = FILE.open(pwmChipFile.getPath() + CHIP_NPWM_PATH, FileFlag.O_RDONLY);
            var maxChannel = getIntegerContent(FILE.read(npwmFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            FILE.close(npwmFd);
            if (pwmBusNumber > maxChannel - 1) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile.getPath() + "' cannot be exported! Max available channel is " + maxChannel);
            }
            var exportFd = FILE.open(pwmChipFile.getPath() + CHIP_EXPORT_PATH, FileFlag.O_WRONLY);
            FILE.write(exportFd, getByteContent(pwmBusNumber));
            FILE.close(exportFd);
            if (!pwmFile.exists()) {
                throw new IllegalArgumentException("PWM Bus at path '" + pwmFile.getPath() + "' cannot be exported!");
            }
        }
        this.pwmPath = pwmFile.getPath();

        var stateFd = FILE.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        this.onState = getIntegerContent(FILE.read(stateFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)) == 1;
        FILE.close(stateFd);

        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            var dutyCycleFd = FILE.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_RDWR);
            this.dutyCycle = getIntegerContent(FILE.read(dutyCycleFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            FILE.close(dutyCycleFd);
        }

        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            var polarityFd = FILE.open(this.pwmPath + POLARITY_PATH, FileFlag.O_RDWR);
            this.polarity = PwmPolarity.parse(getStringContent(FILE.read(polarityFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)));
            FILE.close(polarityFd);
        }

        if (config.frequency() != null) {
            this.frequency = config.frequency();
            this.period = NANOS_IN_SECOND / this.frequency;
        } else {
            var periodFd = FILE.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDWR);
            this.period = getIntegerContent(FILE.read(periodFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            FILE.close(periodFd);
        }

        logger.debug("{} - pwm setup finished. Initial state: {}", pwmPath, this);
        return this;
    }

    @Override
    public Pwm on() throws IOException {
        if (onState) {
            logger.warn("{} - PWM Bus is already enabled.", pwmPath);
            throw new Pi4JException("PWM Bus is already enabled.");
        }
        if (frequency < 0) {
            logger.error("{} - cannot set frequency '{}', required more then 0.", pwmPath, frequency);
            throw new Pi4JException("cannot set frequency '" + frequency + "', required more then 0.");
        }
        var period = (NANOS_IN_SECOND / frequency);
        logger.debug("{} - period is '{}', dutyCycle is '{}' and polarity '{}'.", pwmPath, period, dutyCycle, polarity);

        var periodFd = FILE.open(this.pwmPath + PERIOD_PATH, FileFlag.O_WRONLY);
        var dutyCycleFd = FILE.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_WRONLY);
        var polarityFd = FILE.open(this.pwmPath + POLARITY_PATH, FileFlag.O_WRONLY);

        FILE.write(periodFd, String.valueOf(period).getBytes());
        FILE.write(dutyCycleFd, String.valueOf(dutyCycle).getBytes());
        FILE.write(polarityFd, polarity.getName().getBytes());

        FILE.close(dutyCycleFd);
        FILE.close(periodFd);
        FILE.close(polarityFd);

        var enableFd = FILE.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        FILE.write(enableFd, String.valueOf(1).getBytes());
        FILE.close(enableFd);
        this.onState = true;
        return this;
    }

    @Override
    public Pwm off() throws IOException {
        if (!onState) {
            logger.warn("{} - PWM Bus is already disabled.", pwmPath);
            throw new IllegalStateException("PWM Bus is already disabled.");
        }
        var enableFd = FILE.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        FILE.write(enableFd, String.valueOf(0).getBytes());
        FILE.close(enableFd);
        this.onState = false;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public int getActualFrequency() {
        return (int) (period / NANOS_IN_SECOND);
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


}
