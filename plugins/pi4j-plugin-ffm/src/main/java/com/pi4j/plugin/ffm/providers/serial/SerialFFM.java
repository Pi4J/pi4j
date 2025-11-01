package com.pi4j.plugin.ffm.providers.serial;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.serial.*;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.serial.*;

import java.nio.ByteBuffer;

public class SerialFFM extends SerialBase implements Serial {

    private final FileDescriptorNative FILE = new FileDescriptorNative();
    private final IoctlNative IOCTL = new IoctlNative();

    private int fd;

    public SerialFFM(SerialProvider provider, SerialConfig config) {
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serial initialize(Context context) throws InitializeException {

        this.fd = FILE.open(config.port(), FileFlag.O_RDWR);
        FILE.flock(fd, FileFlag.LOCK_EX);

        var termios = IOCTL.call(fd, Command.getTermiosGet(), Termios2.createEmpty());

//        System.out.println("Installed: " + termios);

        var cflag = termios.c_cflag();
        var iflag = termios.c_iflag();
        var lflag = termios.c_lflag();
        var oflag = termios.c_oflag();
        var ispeed = termios.c_ispeed();
        var ospeed = termios.c_ospeed();
        if (config.parity() != null) {
            if (!config.parity().equals(Parity.NONE)) {
                cflag &= ~ControlMode.PARENB.value();
            } else {
                cflag |= ControlMode.PARENB.value();
            }
        } else {
            cflag &= ~ControlMode.PARENB.value();
        }
        if (config.stopBits() != null) {
            if (config.stopBits().equals(StopBits._2)) {
                cflag |= ControlMode.CSTOPB.value();
            } else {
                cflag &= ~ControlMode.CSTOPB.value();
            }
        } else {
            cflag &= ~ControlMode.CSTOPB.value();
        }
        if (config.dataBits() != null) {
            cflag &= ~ControlMode.CSIZE.value();
            switch (config.dataBits()) {
                case _5 -> cflag |= ControlMode.CS5.value();
                case _6 -> cflag |= ControlMode.CS6.value();
                case _7 -> cflag |= ControlMode.CS7.value();
                case _8 -> cflag |= ControlMode.CS8.value();
            }
        } else {
            cflag &= ~ControlMode.CSIZE.value();
            cflag |= ControlMode.CS8.value();
        }
        if (config.flowControl() != null) {
            switch (config.flowControl()) {
                case NONE, SOFTWARE -> cflag &= ~ControlMode.CRTSCTS.value();
                case HARDWARE -> {
                    cflag |= ControlMode.CRTSCTS.value();
                    iflag &= ~(InputMode.IXON.value() | InputMode.IXOFF.value() | InputMode.IXANY.value());
                }
            }
        } else {
            cflag &= ~ControlMode.CRTSCTS.value();
            iflag &= ~(InputMode.IXON.value() | InputMode.IXOFF.value() | InputMode.IXANY.value());
        }
        if (config.baud() != null) {
            cflag &= ~ControlMode.CBAUD.value();
            cflag |= ControlMode.CBAUDEX.value();
            ospeed = config.baud();

            cflag &= ~(ControlMode.CBAUD.value() << ControlMode.IBSHIFT.value());
            cflag |= ControlMode.CBAUDEX.value() << ControlMode.IBSHIFT.value();
            ispeed = config.baud();
        }

        cflag |= ControlMode.CREAD.value() | ControlMode.CLOCAL.value();

        lflag &= ~LocalMode.ICANON.value();

        lflag &= ~LocalMode.ECHO.value();
        lflag &= ~LocalMode.ECHOE.value();
        lflag &= ~LocalMode.ECHONL.value();

        lflag &= ~LocalMode.ISIG.value();

        iflag &= ~(InputMode.IGNBRK.value() | InputMode.BRKINT.value() | InputMode.PARMRK.value() | InputMode.ISTRIP.value() | InputMode.INLCR.value() | InputMode.IGNCR.value() | InputMode.ICRNL.value());

        oflag &= ~OutputMode.OPOST.value();
        oflag &= ~OutputMode.ONLCR.value();

        termios = new Termios2(iflag, oflag, cflag, lflag, termios.c_line(), termios.c_cc(), ispeed, ospeed);

        //      System.out.println("Prepared: " + termios);

        IOCTL.call(fd, Command.getTermiosSet(), termios);

//        termios = IOCTL.call(fd, Command.getTermiosGet(), Termios2.createEmpty());
//
//        System.out.println("Saved: " + termios);

        return super.initialize(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return IOCTL.call(fd, Command.getFIONREAD(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        FILE.flock(fd, FileFlag.LOCK_UN);
        FILE.close(fd);
        super.close();
    }

    // -------------------------------------------------------------------
    // DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return FILE.write(fd, new byte[]{b});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        return FILE.write(fd, data);
    }


    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        var buffer = new byte[1];
        buffer = FILE.read(fd, buffer, buffer.length);
        return buffer[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        var bytes = FILE.read(fd, buffer, buffer.length);
        ByteBuffer.wrap(buffer).put(bytes);
        return bytes.length;
    }
}
