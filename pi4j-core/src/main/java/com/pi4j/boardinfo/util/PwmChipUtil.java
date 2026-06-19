package com.pi4j.boardinfo.util;

/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  PwmChipUtil.java
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Utility for determining which sysfs {@code pwmchip} exposes the user-accessible PWM channels on a
 * Raspberry Pi. On boards with an RP1 controller it inspects the PWM filesystem to find the matching
 * chip number; if no user-accessible PWM is found it falls back to {@link #DEFAULT_RP1_PWM_CHIP}.
 * Used by {@link BoardInfoHelper#getPwmChipAddress()} to resolve the correct chip for the running board.
 */
public class PwmChipUtil {

    /**
     * Default sysfs directory that exposes the available PWM chips ({@code /sys/class/pwm}).
     */
    public static String DEFAULT_PWM_SYSTEM_PATH = "/sys/class/pwm";

    /**
     * PWM chip number used on Raspberry Pi models prior to the RP1 controller, where the
     * user-accessible PWM is always {@code pwmchip0}.
     */
    public static int DEFAULT_LEGACY_PWM_CHIP = 0;

    /**
     * Default PWM chip number assumed for RP1-based boards when no chip can be detected from the
     * PWM filesystem ({@code pwmchip2}).
     */
    public static int DEFAULT_RP1_PWM_CHIP = 2;

    /**
     * General default PWM chip number, equal to {@link #DEFAULT_RP1_PWM_CHIP}.
     */
    public static int DEFAULT_PWM_CHIP = DEFAULT_RP1_PWM_CHIP;

    /**
     * The ID of the PWM chip:
     *
     * <ul>
     *     <li>System without RP1 (pre-RPi 5): 0</li>
     *     <li>System with RP1 (RPi 5 and later): as detected or {@link #DEFAULT_RP1_PWM_CHIP}</li>
     * </ul>
     *
     * @return {@code int} value for the pwmchip
     */
    public static int getPWMChip() {
        if (!BoardInfoHelper.usesRP1()) {
            return 0;
        }
        return getPWMChipForRP1(DEFAULT_PWM_SYSTEM_PATH);
    }

    /**
     * Detects the user-accessible PWM chip on an RP1-based board, scanning the default PWM filesystem
     * path {@link #DEFAULT_PWM_SYSTEM_PATH}.
     *
     * @return the detected {@code pwmchip} number, or {@link #DEFAULT_RP1_PWM_CHIP} if none was found
     */
    public static int getPWMChipForRP1() {
        return getPWMChipForRP1(DEFAULT_PWM_SYSTEM_PATH);
    }

    /**
     * Detects the user-accessible PWM chip on an RP1-based board by walking the given PWM filesystem
     * directory and matching the RP1 PWM device address against its symbolic-link targets.
     *
     * @param pwmFileSystemPath the sysfs directory listing the PWM chips, typically {@code /sys/class/pwm}
     * @return the detected {@code pwmchip} number, or {@link #DEFAULT_RP1_PWM_CHIP} if the path is
     *         missing, unreadable, or contains no matching chip
     */
    public static int getPWMChipForRP1(String pwmFileSystemPath) {
        Logger logger = LoggerFactory.getLogger(PwmChipUtil.class);
        int pwmChip = DEFAULT_RP1_PWM_CHIP;

        try {
            Path pwmPath = Paths.get(pwmFileSystemPath);
            if (!Files.exists(pwmPath)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PWM filesystem path does not exist: {}", pwmFileSystemPath);
                }
                return pwmChip;
            }

            List<String> pathEntries = Files.walk(pwmPath, 1)
                .filter(path -> !path.equals(pwmPath)) // exclude the root directory
                .map(path -> {
                    try {
                        return Files.readSymbolicLink(path).toString();
                    } catch (IOException e) {
                        // If it's not a symbolic link or can't be read, return the path name
                        return path.getFileName().toString();
                    }
                })
                .toList();

            var foundChipNum = parsePWMPaths(pathEntries);
            if (foundChipNum.isPresent()) {
                pwmChip = foundChipNum.get();
                if (logger.isDebugEnabled()) {
                    logger.debug("Detected PWM chip {} ", pwmChip);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Did NOT detect PWM chip for RP1 on paths {}", pathEntries);
                }
            }
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error reading PWM filesystem path {}: {}", pwmFileSystemPath, e.getMessage());
            }
        }

        return pwmChip;
    }

    /**
     * Parses the symbolic-link targets (or file names) listed under the PWM filesystem path and
     * extracts the {@code pwmchip} number whose device address matches the RP1 user PWM block.
     *
     * @param paths the entries found in the PWM filesystem directory, as link targets or file names
     * @return an {@link Optional} holding the matching {@code pwmchip} number, or empty if no entry matches
     */
    public static Optional<Integer> parsePWMPaths(List<String> paths) {
        var pwmChipIdentifier = "pwmchip";
        for (String path : paths) {
            String chipNum = "";
            StringBuilder chipName = new StringBuilder(pwmChipIdentifier);

            // Test for the RP1 chip address for the user PWM channels
            if (path.contains("1f00098000")) {
                int numStart = path.indexOf(pwmChipIdentifier) + chipName.length();
                while (Character.isDigit(path.substring(numStart, numStart + 1).charAt(0))) {
                    chipName.append(path.charAt(numStart));
                    chipNum = new StringBuilder().append(chipNum.substring(0, chipNum.length())).append(path.substring(numStart, numStart + 1)).toString();
                    numStart++;
                    if (numStart == path.length())
                        break;
                }
                return Optional.of(Integer.parseInt(chipNum));
            }
        }

        return Optional.empty();
    }
}
