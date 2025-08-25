package com.pi4j.plugin.linuxfs.provider.pwm;


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


import com.pi4j.boardinfo.util.command.CommandResult;
import com.pi4j.plugin.linuxfs.internal.LinuxPwm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

/**
 * LinuxFsPwmUtil
 * PWM Utilities used by a Pi with the RP1 chip.  Will determine if a user
 * accessible PWM is configured, if so, return an Int identifying which pwmchip
 * If no user accessible PWM found, will return the original default value of 2.
 */


public class LinuxFsPwmUtil {

    /**
     * getPWMChipForRP1
     *
     * @param pwmFileSystemPath Device tree path to PWM chips
     *                          typically /sys/class/pwm
     * @return Int value for the pwmchip if configured,
     * else default to original expected value of 2.
     */


    public static int getPWMChipForRP1(String pwmFileSystemPath) {
        Logger logger = LoggerFactory.getLogger(LinuxFsPwmUtil.class);
        int pwmChip = LinuxPwm.DEFAULT_RP1_PWM_CHIP;

        // init to original bookworm using pwmChip2, test if different
        String command = "ls -l " + pwmFileSystemPath;
        CommandResult rslt = execute(command);
        String[] paths = rslt.getOutputMessage().split("\n");
        var foundChipNum = parsePWMPaths(paths);
        if (foundChipNum.isPresent()) {
            pwmChip = foundChipNum.get();
            if (logger.isDebugEnabled()) {
                logger.debug("Detected PWM chip {} ", pwmChip);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Did NOT detect PWM chip for RP1 on paths {}", Arrays.toString(paths));
            }
        }

        return pwmChip;
    }

    /**
     * @param paths Array containing all objects found in pwmFileSystemPath
     * @return Optional containing an Int if successful, else an empty
     */
    public static Optional<Integer> parsePWMPaths(String[] paths) {
        var pwmChipIdentifier = "pwmchip";
        for (int counter = 0; counter < paths.length; counter++) {
            String chipNum = "";
            StringBuilder chipName = new StringBuilder(pwmChipIdentifier);

            // Test for the RP1 chip address for the user PWM channels
            if (paths[counter].contains("1f00098000")) {
                int numStart = paths[counter].indexOf(pwmChipIdentifier) + chipName.length();
                while (Character.isDigit(paths[counter].substring(numStart, numStart + 1).charAt(0))) {
                    chipName.append(paths[counter].charAt(numStart));
                    chipNum = new StringBuilder().append(chipNum.substring(0, chipNum.length())).append(paths[counter].substring(numStart, numStart + 1)).toString();
                    numStart++;
                }
                return Optional.of(Integer.parseInt(chipNum));
            }
        }

        return Optional.empty();
    }
}
