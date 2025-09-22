package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.permission.PermissionNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Properties;

/**
 * Helper class to check permissions needed to run with FFM API
 */
public class PermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);
    private static final String OS_NAME_KEY = "NAME";

    private static final boolean RUN_AS_SUDO = System.getenv("SUDO_COMMAND") != null;
    private static final String CURRENT_USER =  System.getProperty("user.name");

    private static final PermissionNative PERMISSION_NATIVE = new PermissionNative();
    private static boolean isDebian = false;

    /**
     * Checks user permissions to run pi4j.
     * - run with `sudo`. Prints warning message.
     * - presence of `dialout` or `gpio` groups (depending on operating system). Throws an exception.
     */
    public static void checkUser() {
        // check if running with sudo
        // all access should be good, but this is nit safe!
        if (RUN_AS_SUDO) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*       Pi4J provider is running using `sudo`       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit https://                   *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }
        //check user belongs to 'dialout' or 'gpio' groups
        var groupIds = PERMISSION_NATIVE.getGroupList(CURRENT_USER);
        var groupList = Arrays.stream(groupIds).mapToObj(PERMISSION_NATIVE::getGroupData).toList();

        var osRelease = new Properties();
        try {
            osRelease.load(Files.newInputStream(Paths.get("/etc/os-release")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var osName = osRelease.getProperty(OS_NAME_KEY).toLowerCase();

        if (osName.contains("ubuntu")) {
            // any ubuntu
            if (groupList.stream().noneMatch(g -> new String(g.grName()).equals("dialout"))) {
                logger.error("Permissions for devices are not properly setup. Current user '{}' does not belong to group 'dialout'.", CURRENT_USER);
                logger.error("You can run one liner script '...' or visit http://... for more information.");
                throw new Pi4JException("Permissions are not set up correctly");
            }
        } else if (osName.contains("bian")) {
            // Raspbian / Armbian / Debian
            isDebian = true;
            if (groupList.stream().noneMatch(g -> new String(g.grName()).equals("gpio"))) {
                logger.error("Permissions for devices are not properly setup. Current user '{}' does not belong to group 'gpio'.", CURRENT_USER);
                logger.error("You can run one liner script '...' or visit http://... for more information.");
                throw new Pi4JException("Permissions are not set up correctly");
            }
        } else {
            logger.warn("Cannot detect OS name '{}'. Pi4j might not work without correctly installed permissions.", osName);
        }
    }

    /**
     * Checks device permissions to run the provider.
     * - check device existence. Throws exception
     * - checks group `dialout`/`gpio` and group permissions to write/read. Throws exception.
     * @param devicePath path to be checked
     */
    public static void checkDevice(String devicePath) {
        var path = Paths.get(devicePath);
        if (!path.toFile().exists()) {
            throw new Pi4JException("Device '" + devicePath + "' does not exists.");
        }
        PosixFileAttributes attributes;
        try {
            attributes = Files.getFileAttributeView(path, PosixFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var owner = attributes.owner();
        var group = attributes.group();
        var permissions = attributes.permissions();
        if (owner.getName().equals(CURRENT_USER)) {
            // owner is user nothing to check
            return;
        }
        if (isDebian && !group.getName().equals("gpio")) {
            logger.error("Device '{}' does not belong to group 'gpio'.", devicePath);
            if (!permissions.contains(PosixFilePermission.GROUP_READ) || !permissions.contains(PosixFilePermission.GROUP_WRITE)) {
                logger.error("Device '{}' does not have enough group permissions '{}'. Please, add udev rules by one liner script '...' or visit" +
                    "https://... for more information", devicePath, permissions);
            } else {
                logger.error("Please, add udev rules by one liner script '...' or visit https://... for more information");
            }
            throw new Pi4JException("Permissions are not set up correctly");
        } else if (!isDebian && !group.getName().equals("dialout")) {
            logger.error("Device '{}' does not belong to group 'dialout'.", devicePath);
            if (!permissions.contains(PosixFilePermission.GROUP_READ) || !permissions.contains(PosixFilePermission.GROUP_WRITE)) {
                logger.error("Device '{}' does not have enough group permissions '{}'. Please, add udev rules by one liner script '...' or visit" +
                    "https://... for more information", devicePath, permissions);
            } else {
                logger.error("Please, add udev rules by one liner script '...' or visit https://... for more information");
            }
            throw new Pi4JException("Permissions are not set up correctly");
        }
    }
}
