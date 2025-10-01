package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.permission.PermissionNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Helper class to check permissions needed to run with FFM API
 */
public class PermissionHelper {
    private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);
    private static final String OS_NAME_KEY = "NAME";

    private static final boolean RUN_AS_SUDO = System.getenv("SUDO_COMMAND") != null;
    private static final String CURRENT_USER = System.getProperty("user.name");

    private static final PermissionNative PERMISSION_NATIVE = new PermissionNative();
    private static boolean isDebian = false;
    private static boolean skippedUserCheck = false;

    /**
     * Checks user permissions to run pi4j.
     * - run with `sudo`. Prints warning message.
     * - run as `root`. Prints warning message.
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
        if (CURRENT_USER.equals("root")) {
            logger.warn("* * * * * * * * * * * WARNING * * * * * * * * * * * *");
            logger.warn("*      Pi4J provider is running using as root       *");
            logger.warn("*                 This is not safe!                 *");
            logger.warn("* Please, consider setting up relevant permissions. *");
            logger.warn("* For more, please visit https://                   *");
            logger.warn("* * * * * * * * * * * * * * * * * * * * * * * * * * *");
            return;
        }
        // check user belongs to 'dialout' or 'gpio' groups
        var groupIds = PERMISSION_NATIVE.getGroupList(CURRENT_USER);
        var groupList = Arrays.stream(groupIds).mapToObj(PERMISSION_NATIVE::getGroupData).toList();

        // getting current os release name to get the default name of corresponding groups
        // https://gist.github.com/natefoo/814c5bf936922dad97ff as a reference of '*-release' contents
        var osRelease = new Properties();
        var pattern = Pattern.compile("[A-z0-9-_]+release");
        Path file;
        try (var files = Files.list(Paths.get("/etc/"))) {
            file = files.filter((p) -> pattern.matcher(p.getFileName().toString()).matches())
                .findAny()
                .orElseThrow(() -> new IOException("Could not find any OS release file in '/etc/'"));
            osRelease.load(Files.newInputStream(file));
        } catch (IOException e) {
            logger.warn(e.getMessage());
            skippedUserCheck = true;
            return;
        }
        var osName = osRelease.getProperty(OS_NAME_KEY);
        if (osName == null) {
            logger.warn("There is no '{}' about OS in release file '{}'. Pi4j might not work without correctly installed permissions.", OS_NAME_KEY, file);
            return;
        }
        osName = osName.toLowerCase();
        // checking groups available
        // basically there should be one of two groups - 'dialout' or 'gpio' depending on distro
        if (osName.contains("ubuntu")) {
            // any ubuntu
            if (groupList.stream().noneMatch(g -> new String(g.grName()).equals("dialout"))) {
                logger.error("Permissions for devices are not properly setup. Current user '{}' does not belong to group 'dialout'.", CURRENT_USER);
                logger.error("You can run one liner script '...' or visit http://... for more information.");
                throw new Pi4JException("Permissions are not set up correctly");
            }
        } else if (osName.contains("bian")) {
            // Raspbian / Armbian / Debian / etc
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
     *
     * @param devicePath path to be checked
     */
    public static void checkDevice(String devicePath) {
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
        var group = attributes.group();
        var permissions = attributes.permissions();
        if (owner.getName().equals(CURRENT_USER)) {
            // owner is user nothing to check
            return;
        }

        // we did not find any information about os release, so we cannot assume any suggestions for
        // possible groups
        if (skippedUserCheck) {
            return;
        }

        // check relevant groups
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
