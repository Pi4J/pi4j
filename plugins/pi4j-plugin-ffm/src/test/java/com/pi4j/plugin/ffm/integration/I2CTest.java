package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
@Disabled
public class I2CTest {
    private static final String IN_CONTAINER = System.getenv("IN_CONTAINER");
    private static Context pi4j;
    private static I2C i2c;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/i2c-setup.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to setup I2C Test: \n" + errorOutput + "\n" +
                    "Probably you need to add the I2C bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }

        pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();
        i2c = pi4j.i2c().create(1, 0x1C);

    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/i2c-clean.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to cleanup I2C Test: \n" + errorOutput + "\n" +
                    "Probably you need to add the I2C bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }
    }

    @Test
    public void testI2CCreation() {
        assertEquals(1, i2c.bus());
    }

    @Test
    public void testI2CWriteByte() throws InterruptedException {
        var write = i2c.write(0xEE);
        assertEquals(0, write);
    }

    @Test
    public void testI2CReadByte() throws InterruptedException {
        var read = i2c.read();
        assertEquals(0, read);
    }

    @Test
    public void testI2CWriteReadBlockData() throws InterruptedException {
        var writeBuffer = new byte[] {0x01, 0x02, 0x03};
        var write = i2c.writeRegister(0xFF, writeBuffer);
        assertEquals(0, write);
        var readBuffer = new byte[3];
        var read = i2c.readRegister(0xFF, readBuffer);
        assertEquals(3, read);
        assertArrayEquals(writeBuffer, readBuffer);
    }

    @Test
    public void testI2CWriteReadWordData() throws InterruptedException {
        var write = i2c.writeRegisterWord(0xFF, 1);
        assertEquals(0, write);
        var read = i2c.readRegisterWord(0xFF);
        assertEquals(1, read);
    }

    @Test
    public void testI2CWriteReadRegister() throws InterruptedException {
       var write = i2c.writeRegister(0x324, 0xFF);
       assertEquals(0, write);
       var read = i2c.readRegister(0x324);
       assertEquals(0xFF, read);
    }


    @Test
    public void testConcurrency() throws InterruptedException {
        // So the idea is to make a lot of writes from many threads and then read the last value from register.
        // For controlling threads execution order we are using LinkedList, so no matter what thread is executed last,
        // the result of read register will be always the same as last int in a list.
        // This demonstrates the atomic operations within the linux driver.
        //
        // NOTE: i2c-stub driver used for mock does not have mutex (e.g. https://elixir.bootlin.com/linux/v6.11.7/source/drivers/i2c/i2c-stub.c#L123)
        // That is why synchronized block is added. If using in real i2c-dev driver, you can remove the block and the test should pass as well.
        // See https://elixir.bootlin.com/linux/v6.11.7/source/drivers/i2c/i2c-core-smbus.c#L535 where locking is occurred.
        //
        // list of values filled by threads
        LinkedList<Integer> list = new LinkedList<>();
        // try 200 times, should be enough
        for (int j = 0; j < 200; j++) {
            var threadCount = 20;
            var latch = new CountDownLatch(threadCount);
            try (var service = Executors.newFixedThreadPool(threadCount)) {
                for (int i = 0; i < threadCount; i++) {
                    // effective final
                    var value = i;
                    service.submit(() -> {
                        try {
                            // synchronize with a latch, should work the same if you remove it with real driver
                            synchronized (latch) {
                                // add value to the linked list
                                list.add(value);
                                // write the thread number
                                var write = i2c.writeRegister(0x324, value);
                                assertEquals(0, write);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            // countdown latch for threads to wait in tests
                            latch.countDown();
                        }
                    });
                }
            }
            // wait all threads to finish
            latch.await();
            var read = i2c.readRegister(0x324);
            //System.out.println("Run: " + j + " Last: " + list.getLast());
            assertEquals(list.getLast(), read);
            list.clear();
        }
    }
}
