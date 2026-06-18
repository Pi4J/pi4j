package com.pi4j.plugin.ffm.integration;

import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Throwaway diagnostic that mirrors lineinfo-probe.c through the exact FFM primitives.
 * Assumes the gpio mock is already loaded (run gpio-setup.sh first) and gpiochip97 exists.
 * Run with: mvn -pl plugins/pi4j-plugin-ffm test -Dtest=LineInfoProbeTest
 */
public class LineInfoProbeTest extends BaseSetup {

    @Test
    public void probe() throws IOException, InterruptedException {
        setup("gpio");
        var ioctl = new IoctlNative();
        var file = new FileDescriptorNative();
        var fd = file.open("/dev/gpiochip97", FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
        System.out.println("=== FFM lineinfo probe on /dev/gpiochip97 (fd=" + fd + ") ===");
        System.out.println("LineInfo.LAYOUT.byteSize=" + LineInfo.LAYOUT.byteSize()
            + " ioctl=0x" + Long.toHexString(Command.getGpioV2GetLineInfoIoctl())
            + " USED_mask=0x" + Integer.toHexString(PinFlag.USED.getValue()));
        for (int i = 0; i < 8; i++) {
            var request = new LineInfo(new byte[]{}, new byte[]{}, i, 0, 0, new LineAttribute[]{});
            var result = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), request);
            System.out.printf("requested=%d -> returned offset=%d flags=0x%x USED=%b consumer='%s'%n",
                i, result.offset(), result.flags(),
                (result.flags() & PinFlag.USED.getValue()) != 0,
                new String(result.consumer()));
        }
        file.close(fd);
        tearDown("gpio");
    }
}
