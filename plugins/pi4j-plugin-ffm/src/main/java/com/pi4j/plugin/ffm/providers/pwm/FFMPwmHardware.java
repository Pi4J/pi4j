package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.FileWatcher;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.util.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Hardware {@link Pwm} implementation backed by the Linux sysfs PWM interface under
 * {@code /sys/class/pwm/pwmchipN}. The channel is exported on demand and its {@code period},
 * {@code duty_cycle}, {@code polarity} and {@code enable} attributes are driven by reading and writing
 * the corresponding text files via {@link FileDescriptorNative}.
 * <p>
 * Because sysfs attributes appear asynchronously after an export (the udev rules must apply first), the
 * implementation waits for read/write access to each attribute file before using it.
 *
 * @see com.pi4j.io.pwm.Pwm
 * @see FFMPwmProviderImpl
 */
public class FFMPwmHardware extends PwmBase implements Pwm {
    private final Logger logger = LoggerFactory.getLogger(FFMPwmHardware.class);

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

    /**
     * Creates a hardware PWM instance for the chip and channel given in the configuration, and verifies
     * that the corresponding {@code /sys/class/pwm/pwmchipN} path is accessible with the required
     * permissions.
     *
     * @param provider the {@link PwmProvider} that created this instance
     * @param config   the PWM configuration supplying the chip number, channel and optional initial
     *                 duty cycle, polarity and frequency
     */
    public FFMPwmHardware(PwmProvider provider, PwmConfig config) {
        super(provider, config);
        this.chip = config.chip();
        this.channel = config.channel();
        FFMPermissionHelper.checkDevicePermissions(CHIP_PATH + chip, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Exports the configured PWM channel if it is not already present (writing the channel number to the
     * chip's {@code export} file after validating it against {@code npwm}, then waiting for the channel
     * directory to appear), and seeds the cached on-state, duty cycle, polarity and period from either
     * the configuration or the current sysfs attribute values.
     *
     * @throws IllegalArgumentException if the channel exceeds the chip's number of channels or the
     *                                  exported channel directory does not appear within the timeout
     */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        var pwmChipFile = CHIP_PATH + chip;

        var pwmFile = pwmChipFile + PWM_PATH + channel;
        if (deviceNotExists(pwmFile)) {
            logger.trace("{} - no PWM Bus found... will try to export PWM Bus first.", pwmFile);
            try (var pwmFileWatcher = new FileWatcher(Path.of(pwmChipFile), PWM_PATH + channel, 100)) {
                var npwmFd = file.open(pwmChipFile + CHIP_NPWM_PATH, FileFlag.O_RDONLY);
                var maxChannel = getIntegerContent(file.read(npwmFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
                file.close(npwmFd);
                if (channel > maxChannel - 1) {
                    throw new IllegalArgumentException("PWM channel " + channel + " at path '" + pwmFile + "' cannot be exported! Max available channel is " + maxChannel);
                }
                var exportFd = file.open(pwmChipFile + CHIP_EXPORT_PATH, FileFlag.O_WRONLY);
                file.write(exportFd, getByteContent(channel));
                file.close(exportFd);
                if (!pwmFileWatcher.waitForCreation()) {
                    throw new IllegalArgumentException("PWM channel " + channel + " at path '" + pwmFile + "' haven't created within timeout!");
                }
            } catch (java.io.IOException e) {
                throw new Pi4JException(e);
            }
        }
        this.pwmPath = pwmFile;

        waitForReadPermission(this.pwmPath + ENABLE_PATH, 0);
        var stateFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDONLY);
        this.onState = getIntegerContent(file.read(stateFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)) == 1;
        file.close(stateFd);

        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            waitForReadPermission(this.pwmPath + DUTY_CYCLE_PATH, 0);
            var dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_RDONLY);
            this.dutyCycle = getIntegerContent(file.read(dutyCycleFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(dutyCycleFd);
        }

        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            waitForReadPermission(this.pwmPath + POLARITY_PATH, 0);
            var polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_RDONLY);
            this.polarity = PwmPolarity.parse(getStringContent(file.read(polarityFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE)));
            file.close(polarityFd);
        }

        if (config.frequency() != null) {
            this.frequency = config.frequency();
            this.period = Math.round(NANOS_IN_SECOND / this.frequency);
        } else {
            waitForReadPermission(this.pwmPath + PERIOD_PATH, 0);
            var periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDONLY);
            this.period = getIntegerContent(file.read(periodFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
            file.close(periodFd);
        }

        // [INITIALIZE STATE] initialize PWM pin state (via superclass impl)
        super.initialize(context);

        logger.debug("{} - pwm setup finished. Initial state: {}", pwmPath, this.onState ? "on" : "off");
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Recomputes the {@code period} from the configured frequency and the {@code duty_cycle} from the
     * duty percentage, then writes the sysfs attributes in a reset-then-period-then-duty order (zeroing
     * {@code duty_cycle} first) so the intermediate state never violates the kernel's
     * {@code duty_cycle <= period} constraint, before writing the polarity and finally enabling output.
     *
     * @throws Pi4JException if the configured frequency is negative
     */
    @Override
    public Pwm on() throws IOException {
        if (onState) {
            logger.debug("{} - PWM Bus is already enabled. Settings will be re-applied to apply any change.", pwmPath);
        }

        if (frequency < 0) {
            logger.error("{} - cannot set frequency '{}', required more then 0.", pwmPath, frequency);
            throw new Pi4JException("cannot set frequency '" + frequency + "', required more then 0.");
        }

        this.period = Math.round(NANOS_IN_SECOND / frequency);
        var dCycle = Math.round((double) (period * dutyCycle) / 100);
        logger.debug("{} - period is '{}', dutyCycle is '{}' and polarity '{}'.", pwmPath, period, dutyCycle, polarity);

        // NOTE: period and duty_cycle are two separate sysfs writes, each applied independently against
        // the cached state. Lowering the frequency between on() calls (e.g. 100Hz -> 500Hz: period
        // 10ms -> 2ms while duty is still 5ms) makes the intermediate state breach duty_cycle <= period.
        // Older kernels (e.g. 6.8) strictly return -EINVAL for that transient; newer kernels (e.g. 6.17 after
        // the 6.11+ PWM-core rework) tolerate/clamp it. The explicit reset-then-period-then-duty ordering
        // below is correct on both, instead of relying on the newer kernel's leniency.
        waitForReadPermission(this.pwmPath + PERIOD_PATH, 0);
        var currentPeriodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDONLY);
        var currentPeriod = getIntegerContent(file.read(currentPeriodFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
        file.close(currentPeriodFd);
        if (currentPeriod > 0) {
            waitForWritePermission(this.pwmPath + DUTY_CYCLE_PATH, 0);
            var dutyResetFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_WRONLY);
            file.write(dutyResetFd, String.valueOf(0).getBytes());
            file.close(dutyResetFd);
        }

        waitForWritePermission(this.pwmPath + PERIOD_PATH, 0);
        var periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_WRONLY);
        file.write(periodFd, String.valueOf(period).getBytes());
        file.close(periodFd);

        waitForWritePermission(this.pwmPath + DUTY_CYCLE_PATH, 0);
        var dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_WRONLY);
        file.write(dutyCycleFd, String.valueOf(dCycle).getBytes());
        file.close(dutyCycleFd);

        waitForWritePermission(this.pwmPath + POLARITY_PATH, 0);
        var polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_WRONLY);
        file.write(polarityFd, polarity.getName().getBytes());
        file.close(polarityFd);

        waitForWritePermission(this.pwmPath + ENABLE_PATH, 0);
        var enableFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_WRONLY);
        file.write(enableFd, String.valueOf(1).getBytes());
        file.close(enableFd);

        this.onState = true;

        return this;
    }

    @Override
    public Pwm off() throws IOException {
        if (!onState) {
            logger.warn("{} - PWM is already disabled.", pwmPath);
            return this;
        }
        var enableFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        file.write(enableFd, String.valueOf(0).getBytes());
        file.close(enableFd);
        this.onState = false;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * When the configuration defines a shutdown value it is applied through the superclass; otherwise
     * the channel is released by writing its number to the chip's {@code unexport} sysfs file.
     */
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
    private void waitForReadPermission(String path, int timeout) {
        if (timeout > 100) {
            throw new Pi4JException("Timeout occurred while waiting for file");
        }
        logger.trace("{} - Waiting for file '{}' for {}ms", pwmPath, path, timeout);
        var access = file.access(path, FileFlag.R_OK);
        if (access != 0) {
            var deferredDelay = new Delay();
            deferredDelay.setMillis(10);
            deferredDelay.materialize();
            waitForReadPermission(path, timeout + 10);
        }
    }

    /**
     * Waits the udev rules to be applied for 100ms at most.
     *
     * @param path    path of the file
     * @param timeout counting timeout
     */
    private void waitForWritePermission(String path, int timeout) {
        if (timeout > 100) {
            throw new Pi4JException("Timeout occurred while waiting for file");
        }
        logger.trace("{} - Waiting for file '{}' for {}ms", pwmPath, path, timeout);
        var access = file.access(path, FileFlag.W_OK);
        if (access != 0) {
            var deferredDelay = new Delay();
            deferredDelay.setMillis(10);
            deferredDelay.materialize();
            waitForWritePermission(path, timeout + 10);
        }
    }
}
