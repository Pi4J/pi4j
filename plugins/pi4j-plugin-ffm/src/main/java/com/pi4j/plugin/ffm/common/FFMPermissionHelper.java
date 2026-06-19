package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.ffm.common.permission.PermissionNative;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import com.pi4j.provider.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static helper that validates the runtime permissions required by the FFM native backend before a
 * provider opens a kernel device. It inspects POSIX users and groups (via {@link PermissionNative},
 * which wraps the glibc group database) and the POSIX file attributes of device nodes such as
 * {@code /dev/gpiochip0}, {@code /dev/i2c-1} or {@code /dev/spidev0.0}, and raises a
 * {@link Pi4JException} when the configuration would prevent native access. Used by the FFM
 * {@link ProviderBase} implementations during initialization.
 */
public class FFMPermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(FFMPermissionHelper.class);

    private static final boolean RUN_AS_SUDO = System.getenv("SUDO_COMMAND") != null;
    private static final String CURRENT_USER = System.getProperty("user.name");

    private static final PermissionNative PERMISSION_NATIVE = new PermissionNative();

    private static List<String> osGroups;
    private static List<String> userGroups;

    /**
     * Checks user permissions to run pi4j:
     * - run with `sudo`. Prints warning message, skip checks
     * - run as `root`. Prints warning message, skip checks
     * - presence of hardware related groups (gpio/dialout, input, i2c, spi). Throws an exception.
     * - current user is a member of hardware related groups. Throws an exception.
     * <p>
     * The required group set depends on the concrete provider type: GPIO/PWM providers need
     * {@code gpio} or {@code dialout}, I2C needs {@code i2c}, and SPI needs {@code spi}.
     *
     * @param provider the FFM provider whose access requirements determine which OS groups are checked
     * @throws Pi4JException if no matching group exists on the system, if the current user does not
     *                       belong to one, or if the provider type is not recognized
     */
    public static void checkUserPermissions(ProviderBase<?, ?, ?> provider) {
        // check if running with sudo
        // all access should be good, but this is not safe!
        if (RUN_AS_SUDO) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*       Pi4J provider is running using `sudo`       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit:                           *");
            logger.warn("* https://www.pi4j.com/documentation/providers/ffm/ *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }

        if (CURRENT_USER.equals("root")) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*      Pi4J provider is running using as root       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit:                           *");
            logger.warn("* https://www.pi4j.com/documentation/providers/ffm/ *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }

        if (osGroups == null) {
            // opens database with groups
            PERMISSION_NATIVE.openGroupDatabase();

            osGroups = new ArrayList<>();

            // fill groups
            var group = PERMISSION_NATIVE.getNextGroup();
            while (group != null) {
                osGroups.add(new String(group.grName()));
                group = PERMISSION_NATIVE.getNextGroup();
            }

            // closes database with groups
            PERMISSION_NATIVE.closeGroupDatabase();
        }

        if (userGroups == null) {
            // gets user groups
            var userGroupIds = PERMISSION_NATIVE.getGroupList(CURRENT_USER);
            userGroups = Arrays.stream(userGroupIds).mapToObj(PERMISSION_NATIVE::getGroupData).map(g -> new String(g.grName())).toList();
        }

        // checking groups existence and user belonging to the groups
        switch (provider) {
            case FFMDigitalInputProviderImpl _, FFMDigitalOutputProviderImpl _, FFMPwmProviderImpl _ ->
                checkGroups(osGroups, userGroups, "gpio", "dialout");
            case FFMI2CProviderImpl _ -> checkGroups(osGroups, userGroups, "i2c");
            case FFMSpiProviderImpl _ -> checkGroups(osGroups, userGroups, "spi");
            default -> throw new Pi4JException("Unknown provider " + provider);
        }
    }

    private static void checkGroups(List<String> osGroups, List<String> userGroups, String... groupNames) {
        var set = Arrays.asList(groupNames);
        var groupPresent = osGroups.stream().anyMatch(set::contains);
        var groupString = Arrays.toString(groupNames).replace("[", "").replace("]", "");
        if (!groupPresent) {
            logger.error("* * * No groups for provider is present in the system   * * *");
            logger.error("* You can run `sudo groupadd {}` and `sudo useradd {} {}`   *", groupString, CURRENT_USER, groupString);
            logger.error("* Scripts to configure this, are available on:              *");
            logger.error("* https://github.com/pi4J/pi4j-os (check the README)        *");
            logger.error("* * * * * * Do not forget to reboot the device!   * * * * * *");
            throw new Pi4JException("No suitable user group present for provider. Should be " + Arrays.toString(groupNames));
        }
        var userInGroup = userGroups.stream().anyMatch(set::contains);
        if (!userInGroup) {
            logger.error("* * * Current user does not belong to required groups!  * * *");
            logger.error("* You can run `sudo useradd {} {}`                          *", CURRENT_USER, groupString);
            logger.error("* Scripts to configure this, are available on:              *");
            logger.error("* https://github.com/pi4J/pi4j-os (check the README)        *");
            logger.error("* * * * * * Do not forget to reboot the device!   * * * * * *");
            throw new Pi4JException("Current user '" + CURRENT_USER + "' is not member of groups " + Arrays.toString(groupNames));
        }
    }

    /**
     * Checks device permissions to run the provider.
     * - check device existence. Throws exception
     * - check user is not running 'sudo' or 'root'. Skip checks.
     * - checks user is owner of device. Skip checks.
     * - checks device has group permissions to write/read. Throws exception.
     * - checks device does not have other permissions. Prints warning.
     * <p>
     * The expected owning group depends on the hardware interface described by {@code config}:
     * {@code gpio}/{@code dialout} for digital and PWM devices, {@code i2c} for I2C, and {@code spi}
     * for SPI.
     *
     * @param devicePath absolute path of the device node to check, e.g. {@code /dev/i2c-1}
     * @param config     the I/O configuration that identifies the hardware interface and thus the
     *                   expected owning group
     * @throws Pi4JException if the device does not exist, its attributes cannot be read, it does not
     *                       belong to the expected group, it lacks group read/write permission, or
     *                       the configuration type is not recognized
     */
    public static void checkDevicePermissions(String devicePath, IOConfig<?> config) {
        var path = Paths.get(devicePath);
        // checking that device is physically exists
        if (!path.toFile().exists()) {
            throw new Pi4JException("Device '" + devicePath + "' does not exists.");
        }

        // checking if you are running with `sudo` or `root`
        // not safe but should work
        if (RUN_AS_SUDO || CURRENT_USER.equals("root")) {
            return;
        }

        PosixFileAttributes attributes;
        try {
            // get device fs permissions
            attributes = Files.getFileAttributeView(path, PosixFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
        var owner = attributes.owner();
        if (owner.getName().equals(CURRENT_USER)) {
            // owner is user nothing to check
            return;
        }
        var group = attributes.group();
        // gets group and other permissions
        var groupPermissions = attributes.permissions().stream().filter(p -> p.name().startsWith("GROUP_")).toList();
        var otherPermissions = attributes.permissions().stream().filter(p -> p.name().startsWith("OTHER_")).toList();

        // show warning, if any other permissions are set
        if (!otherPermissions.isEmpty()) {
            logger.warn("Device '{}' has excessive permissions for others: {}", devicePath, otherPermissions);
        }

        // we need to check permissions of a device depending on hardware interface
        switch (config) {
            case DigitalInputConfig _, DigitalOutputConfig _, PwmConfig _ -> {
                if (!group.getName().equals("gpio") && !group.getName().equals("dialout")) {
                    printError(config instanceof PwmConfig ? "pwm" : "gpio");
                    throw new Pi4JException("Device '" + devicePath + "' (" + owner + ":" + group + ") does not belong to group 'gpio' or 'dialout'.");
                }
            }
            case I2CConfig _ -> {
                if (!group.getName().equals("i2c")) {
                    printError("i2c");
                    throw new Pi4JException("Device '" + devicePath + "' (" + owner + ":" + group + ") does not belong to group 'i2c'.");
                }
            }
            case SpiConfig _ -> {
                if (!group.getName().equals("spi")) {
                    printError("spi");
                    throw new Pi4JException("Device '" + devicePath + "' (" + owner + ":" + group + ") does not belong to group 'spi'.");
                }
            }
            default -> throw new Pi4JException("Unknown config: " + config);
        }

        // finally, check if device has group write/read permissions
        if (!groupPermissions.contains(PosixFilePermission.GROUP_WRITE) && !groupPermissions.contains(PosixFilePermission.GROUP_READ)) {
            throw new Pi4JException("Device '" + devicePath + "' does not have enough group permissions: got "
                + groupPermissions + ", but have to be " + List.of(PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_READ));
        }
    }

    private static void printError(String hardware) {
        logger.error("* * * * * * * * * Device does not set up correctly! * * * * * * * * *");
        logger.error("* Please, use udev rules to setup device group permissions for '{}' *", hardware);
        logger.error("* For more, please visit:                                           *");
        logger.error("* https://www.pi4j.com/documentation/providers/ffm/                 *");
        logger.error("* Scripts to configure this, are available on:                      *");
        logger.error("* https://github.com/pi4J/pi4j-os (check the README)                *");
    }
}
