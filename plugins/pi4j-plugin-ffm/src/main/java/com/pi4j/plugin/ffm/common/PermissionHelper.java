package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.ffm.common.permission.PermissionNative;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
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
 * Helper class to check permissions needed to run with FFM API
 */
public class PermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);

    private static final boolean RUN_AS_SUDO = System.getenv("SUDO_COMMAND") != null;
    private static final String CURRENT_USER = System.getProperty("user.name");

    private static final PermissionNative PERMISSION_NATIVE = new PermissionNative();

    /**
     * Checks user permissions to run pi4j:
     * - run with `sudo`. Prints warning message, skip checks
     * - run as `root`. Prints warning message, skip checks
     * - presence of hardware related groups (gpio/dialout, input, i2c, spi). Throws an exception.
     * - current user is a member of hardware related groups. Throws an exception.
     */
    public static void checkUserPermissions(ProviderBase<?,?, ?> provider) {
        // check if running with sudo
        // all access should be good, but this is not safe!
        if (RUN_AS_SUDO) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*       Pi4J provider is running using `sudo`       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit https://                   *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }
        if (CURRENT_USER.equals("root")) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*      Pi4J provider is running using as root       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit https://                   *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }

        // opens database with groups
        PERMISSION_NATIVE.openGroupDatabase();

        var osGroups = new ArrayList<String>();

        // fill groups
        var group = PERMISSION_NATIVE.getNextGroup();
        while (group != null) {
            osGroups.add(new String(group.grName()));
            group = PERMISSION_NATIVE.getNextGroup();
        }

        // closes database with groups
        PERMISSION_NATIVE.closeGroupDatabase();

        // gets user groups
        var userGroupIds = PERMISSION_NATIVE.getGroupList(CURRENT_USER);
        var userGroups = Arrays.stream(userGroupIds).mapToObj(PERMISSION_NATIVE::getGroupData).map(g -> new String(g.grName())).toList();

        // checking groups existence and user belonging to the groups
        switch (provider) {
            case DigitalInputFFMProviderImpl _, DigitalOutputFFMProviderImpl _, PwmFFMProviderImpl _ -> checkGroups(osGroups, userGroups, "gpio", "dialout");
            case I2CFFMProviderImpl _ -> checkGroups(osGroups, userGroups, "i2c");
            case SerialFFMProviderImpl _ -> checkGroups(osGroups, userGroups, "serial");
            case SpiFFMProviderImpl _ -> checkGroups(osGroups, userGroups, "spi");
            default -> throw new Pi4JException("Unknown provider " + provider);
        }

    }

    private static void checkGroups(List<String> osGroups, List<String> userGroups, String... groupNames) {
        var set = Arrays.asList(groupNames);
        var groupPresent = osGroups.stream().anyMatch(set::contains);
        var groupString = Arrays.toString(groupNames).replace("[", "").replace("]", "");
        if (!groupPresent) {
            logger.error("* * * No groups for provider is present in the system * * *");
            logger.error("* You can run `sudo groupadd {}` and `sudo useradd {} {}` *", groupString, CURRENT_USER, groupString);
            logger.error("* * * * * * Do not forget to reboot the device! * * * * * *");
            throw new Pi4JException("No suitable user group present for provider. Should be " + Arrays.toString(groupNames));
        }
        var userInGroup = userGroups.stream().anyMatch(set::contains);
        if (!userInGroup) {
            logger.error("* * * Current user does not belong to required groups! * * *");
            logger.error("*             You can run `sudo useradd {} {}`             *", CURRENT_USER, groupString);
            logger.error("* * * * * * Do not forget to reboot the device!  * * * * * *");
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
     *
     * @param devicePath path to be checked
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
            logger.warn("Device '{}' has excessive permissions for others: {}", devicePath , otherPermissions);
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
            case SerialConfig _ -> {
                if (!group.getName().equals("serial")) {
                    printError("serial");
                    throw new Pi4JException("Device '" + devicePath + "' (" + owner + ":" + group + ") does not belong to group 'serial'.");
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
        logger.error("* One liner script: `sudo /bin/bash -c \"$(curl -fsSL https://raw.githubusercontent.com/{}_install.sh)\"` *", hardware);
        logger.error("* More information: https://... *");
    }
}
