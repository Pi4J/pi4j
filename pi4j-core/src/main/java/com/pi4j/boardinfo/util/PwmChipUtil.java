package com.pi4j.boardinfo.util;

/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  LinuxFsPwmUtil.java
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
 * PWM Util used to determine the PWM address on a Raspberry Pi.
 * This will determine if a user accessible PWM is configured, if so, returns an Int identifying which pwmchip
 * If no user accessible PWM found, will return the original default value of 2.
 */
public class PwmChipUtil {

    /**
     * Constant <code>DEFAULT_SYSTEM_PATH="/sys/class/pwm"</code>
     */
    public static String DEFAULT_PWM_SYSTEM_PATH = "/sys/class/pwm";

    /** Constant <code>DEFAULT_LEGACY_PWM_CHIP=0</code> */
    /**
     * In Pi Models Previous to RP1 the chip is number 0
     */
    public static int DEFAULT_LEGACY_PWM_CHIP = 0;

    /** Constant <code>DEFAULT_RP1_PWM_CHIP=2</code> */
    /**
     * In RP1 the chip is number 2
     */
    public static int DEFAULT_RP1_PWM_CHIP = 2;

    /** Constant <code>DEFAULT_PWM_CHIP=2</code> */
    /**
     * In RP1 the chip is number 2
     */
    public static int DEFAULT_PWM_CHIP = DEFAULT_RP1_PWM_CHIP;

    /**
     * getPWMChipForRP1
     *
     * @param pwmFileSystemPath Device tree path to PWM chips
     *                          typically /sys/class/pwm
     * @return Int value for the pwmchip if configured,
     * else default to original expected value of 2.
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
     * @param paths Array containing all objects found in pwmFileSystemPath
     * @return Optional containing an Int if successful, else an empty
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
                        break ;
                }
                return Optional.of(Integer.parseInt(chipNum));
            }
        }

        return Optional.empty();
    }
}
