package com.pi4j.plugin;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class BaseSetup {

    /**
     * Creates new GPIO/I2C/PWM/SPI interface with provided script in resources folder.
     * Implementation notes:
     * - this method uses 'root' access, because of insmod/rmmod calls
     * - to establish passwordless execution developer should add the 'resources' folder to 'sudoers' file
     *
     * @param hwInterface - string representation of interface, one of "gpio", "i2c", "pwm" or "spi"
     * @throws IOException          if any problems occurred while opening folder or script
     * @throws InterruptedException if any problems occurred while executing script
     */
    protected static void setup(String hwInterface) throws IOException, InterruptedException {
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/" + hwInterface + "-setup.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup " + hwInterface + " test:\n" + errorOutput + "\n" +
                "Probably you need to add the bash script path to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }


    /**
     * Tears down GPIO/I2C/PWM/SPI interface with provided script in resources folder.
     * Implementation notes:
     * - this method uses 'root' access, because of insmod/rmmod calls
     * - to establish passwordless execution developer should add the 'resources' folder to 'sudoers' file
     *
     * @param hwInterface - string representation of interface, one of "gpio", "i2c", "pwm" or "spi"
     * @throws IOException          if any problems occurred while opening folder or script
     * @throws InterruptedException if any problems occurred while executing script
     */
    protected static void tearDown(String hwInterface) throws IOException, InterruptedException {
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/" + hwInterface + "-clean.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to clean up " + hwInterface + " test:\n" + errorOutput + "\n" +
                "Probably you need to add the bash script path to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

}
