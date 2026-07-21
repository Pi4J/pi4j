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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
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
     * Since the real size of files in sysfs cannot be determined until read, we size the read buffer to
     * hold the widest value we may encounter. The {@code period} and {@code duty_cycle} attributes are
     * expressed in nanoseconds and backed by a 64-bit value in the kernel, so we allow for the full
     * decimal width of a {@code long} (19 digits) plus an optional sign and trailing newline.
     */
    private static final int MAX_FILE_SIZE = 21;
    private static final long NANOS_IN_SECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);

    /** Pre-encoded ASCII payloads for the two constant {@code enable} writes, avoiding per-call allocation. */
    private static final byte[] ENABLE = "1".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] DISABLE = "0".getBytes(StandardCharsets.US_ASCII);

    /** Upper bound (ms) to wait for udev to apply access rules after an export, and the polling step. */
    private static final int PERMISSION_TIMEOUT_MS = 100;
    private static final int PERMISSION_POLL_MS = 10;

    private String pwmPath;
    private final int chip;
    private final int channel;

    /**
     * Set once the exported channel's attribute files have become readable and writable. After that the
     * sysfs permissions are stable for the life of the export, so the hot {@link #on()}/{@link #off()}
     * paths skip the per-call {@code access()} polling entirely.
     */
    private boolean attributesAccessible = false;

    /**
     * Long-lived file descriptors for the exported channel's {@code enable}, {@code period},
     * {@code duty_cycle} and {@code polarity} sysfs attributes. They are opened once in
     * {@link #initialize(Context)} and reused by every {@link #on()}/{@link #off()} call (rewinding to
     * offset 0 before each access) so the hot path no longer pays an {@code open()}/{@code close()}
     * syscall pair — and its native buffer allocations — for every attribute. They are released in
     * {@link #shutdownInternal(Context)}. A value of {@code -1} means "not yet opened".
     */
    private int enableFd = -1;
    private int periodFd = -1;
    private int dutyCycleFd = -1;
    private int polarityFd = -1;

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
                var maxChannel = getLongContent(file.read(npwmFd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE));
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

        // Wait for udev to grant read+write access to the exported attribute files exactly once. Once
        // granted, the permissions are stable, so on()/off() no longer need to poll access() per call.
        ensureAttributesAccessible();

        // Open the attribute files once and keep them open for the life of this instance, so the hot
        // on()/off() paths only read/write (rewinding to 0) instead of open/write/close per attribute.
        this.enableFd = file.open(this.pwmPath + ENABLE_PATH, FileFlag.O_RDWR);
        this.periodFd = file.open(this.pwmPath + PERIOD_PATH, FileFlag.O_RDWR);
        this.dutyCycleFd = file.open(this.pwmPath + DUTY_CYCLE_PATH, FileFlag.O_RDWR);
        this.polarityFd = file.open(this.pwmPath + POLARITY_PATH, FileFlag.O_RDWR);

        this.onState = getLongContent(readAttribute(enableFd)) == 1;

        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            this.dutyCycle = getLongContent(readAttribute(dutyCycleFd));
        }

        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            this.polarity = PwmPolarity.parse(getStringContent(readAttribute(polarityFd)));
        }

        if (config.frequency() != null) {
            this.frequency = config.frequency();
            this.period = Math.round(NANOS_IN_SECOND / this.frequency);
        } else {
            this.period = getLongContent(readAttribute(periodFd));
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

        // period and duty_cycle are two separate sysfs writes, each applied independently against the
        // cached state. Lowering the frequency between on() calls (e.g. 100Hz -> 500Hz: period
        // 10ms -> 2ms while duty is still 5ms) makes the intermediate state breach duty_cycle <= period.
        // Older kernels (e.g. 6.8) strictly return -EINVAL for that transient; newer kernels (e.g. 6.17 after
        // the 6.11+ PWM-core rework) tolerate/clamp it. Unconditionally zeroing duty_cycle first is always
        // valid (0 <= period holds for any period) and is correct on both kernels, so we no longer read
        // back the current period to decide whether the reset is needed.
        writeAttribute(dutyCycleFd, DISABLE);
        writeAttribute(periodFd, getByteContent(period));
        writeAttribute(dutyCycleFd, getByteContent(dCycle));
        writeAttribute(polarityFd, polarity.getName().getBytes(StandardCharsets.US_ASCII));
        writeAttribute(enableFd, ENABLE);

        this.onState = true;

        return this;
    }

    @Override
    public Pwm off() throws IOException {
        if (!onState) {
            logger.warn("{} - PWM is already disabled.", pwmPath);
            return this;
        }
        writeAttribute(enableFd, DISABLE);
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
        // When a shutdown value is configured the superclass drives it through on()/off(), which still
        // need the persistent attribute descriptors, so those are closed only afterwards.
        if (config.getShutdownValue() != null) {
            try {
                return super.shutdownInternal(context);
            } finally {
                closeAttributeFds();
            }
        }

        // No shutdown value: release the descriptors before unexporting the channel.
        closeAttributeFds();

        var exportFd = file.open(CHIP_PATH + chip + CHIP_UNEXPORT_PATH, FileFlag.O_WRONLY);
        file.write(exportFd, getByteContent(channel));
        file.close(exportFd);

        return this;
    }

    /**
     * Reads a single value from a long-lived attribute descriptor, rewinding it to the start first so
     * the whole current value is returned regardless of any prior access.
     *
     * @param fd the persistent attribute descriptor, e.g. {@link #periodFd}
     * @return the raw bytes read from the attribute file (padded with NULs up to {@link #MAX_FILE_SIZE})
     */
    private byte[] readAttribute(int fd) {
        file.lseek(fd, 0, FileFlag.SEEK_SET);
        return file.read(fd, new byte[MAX_FILE_SIZE], MAX_FILE_SIZE);
    }

    /**
     * Writes a value to a long-lived attribute descriptor, rewinding it to the start first so the
     * sysfs store always receives the complete value from offset 0.
     *
     * @param fd    the persistent attribute descriptor, e.g. {@link #enableFd}
     * @param value the pre-encoded ASCII payload to write
     */
    private void writeAttribute(int fd, byte[] value) {
        file.lseek(fd, 0, FileFlag.SEEK_SET);
        file.write(fd, value);
    }

    /**
     * Closes all persistent attribute descriptors that are currently open and resets them to
     * {@code -1}. Safe to call more than once.
     */
    private void closeAttributeFds() {
        for (var fd : new int[]{enableFd, periodFd, dutyCycleFd, polarityFd}) {
            if (fd >= 0) {
                file.close(fd);
            }
        }
        enableFd = periodFd = dutyCycleFd = polarityFd = -1;
    }

    /**
     * Parses a non-negative decimal value straight out of the ASCII bytes read from a sysfs attribute,
     * stopping at the first non-digit (newline or NUL padding). This avoids allocating an intermediate
     * {@link String} and the additional {@code trim()} copy, and returns a {@code long} so that large
     * nanosecond {@code period}/{@code duty_cycle} values (which exceed {@link Integer#MAX_VALUE} for
     * frequencies below ~0.47&nbsp;Hz) are represented without overflow.
     *
     * @param bytes text byte array to be converted, as returned by {@link #readAttribute(String)}
     * @return the parsed value
     * @throws IllegalArgumentException if {@code bytes} is {@code null} or contains no digits
     */
    private static long getLongContent(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes cannot be null");
        }
        long value = 0;
        var seenDigit = false;
        for (var b : bytes) {
            if (b >= '0' && b <= '9') {
                value = value * 10 + (b - '0');
                seenDigit = true;
            } else if (seenDigit) {
                break; // reached the trailing newline / NUL padding after the number
            }
        }
        if (!seenDigit) {
            throw new IllegalArgumentException("No numeric content in '" + getStringContent(bytes) + "'");
        }
        return value;
    }

    /**
     * Decodes the ASCII bytes of a sysfs attribute up to the first NUL/newline into a trimmed string.
     *
     * @param bytes text byte array to be converted
     * @return the decoded, trimmed string content
     */
    private static String getStringContent(byte[] bytes) {
        var length = 0;
        while (length < bytes.length && bytes[length] != 0 && bytes[length] != '\n') {
            length++;
        }
        return new String(bytes, 0, length, StandardCharsets.US_ASCII).trim();
    }

    /**
     * Encodes a numeric value as its ASCII decimal byte representation for writing to a sysfs attribute.
     *
     * @param number value to encode
     * @return the ASCII-encoded decimal bytes
     */
    private static byte[] getByteContent(long number) {
        return Long.toString(number).getBytes(StandardCharsets.US_ASCII);
    }

    private boolean deviceNotExists(String path) {
        var access = file.access(path, FileFlag.F_OK);
        logger.trace("{} - device has access flag '{}'", pwmPath, access);
        return access != 0;
    }

    /**
     * Waits, once, for udev to grant read and write access to every exported attribute file. sysfs
     * attributes appear asynchronously after an export and their permissions settle shortly afterwards;
     * once granted they remain stable for the life of the export, so this is done a single time and the
     * hot {@link #on()}/{@link #off()} paths skip access checks entirely.
     */
    private void ensureAttributesAccessible() {
        if (attributesAccessible) {
            return;
        }
        for (var attribute : List.of(ENABLE_PATH, PERIOD_PATH, DUTY_CYCLE_PATH, POLARITY_PATH)) {
            waitForAccess(this.pwmPath + attribute, FileFlag.R_OK | FileFlag.W_OK);
        }
        attributesAccessible = true;
    }

    /**
     * Polls {@code access()} for the requested mode until it succeeds, waiting up to
     * {@link #PERMISSION_TIMEOUT_MS} in {@link #PERMISSION_POLL_MS} steps for the udev rules to apply.
     *
     * @param path the file to check
     * @param mode the access mode bitmask, e.g. {@code R_OK | W_OK}; see {@link FileFlag}
     * @throws Pi4JException if access is not granted within the timeout
     */
    private void waitForAccess(String path, int mode) {
        for (var waited = 0; file.access(path, mode) != 0; waited += PERMISSION_POLL_MS) {
            if (waited >= PERMISSION_TIMEOUT_MS) {
                throw new Pi4JException("Timeout occurred while waiting for access to file '" + path + "'");
            }
            logger.trace("{} - Waiting for file '{}' for {}ms", pwmPath, path, waited);
            var deferredDelay = new Delay();
            deferredDelay.setMillis(PERMISSION_POLL_MS);
            deferredDelay.materialize();
        }
    }
}
