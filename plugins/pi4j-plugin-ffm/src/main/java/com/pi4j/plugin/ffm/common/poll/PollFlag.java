package com.pi4j.plugin.ffm.common.poll;

/**
 * Event-mask bit flags for the Linux {@code poll(2)} syscall, mirroring the kernel
 * {@code <asm-generic/poll.h>} {@code POLL*} {@code #define}s. They are set in the {@code events} field
 * to request notifications and read back from the {@code revents} field to learn what occurred.
 *
 * @see <a href="https://elixir.bootlin.com/linux/latest/source/include/uapi/asm-generic/poll.h#L6">linux sources</a>
 */
public final class PollFlag {

    /** {@code POLLIN}: data is available to read without blocking. */
    public static final int POLLIN = 0x0001;
    /** {@code POLLPRI}: urgent/priority data (e.g. a GPIO line edge event) is available to read. */
    public static final int POLLPRI = 0x0002;
    /** {@code POLLERR}: an error condition occurred on the descriptor (output only in {@code revents}). */
    public static final int POLLERR = 0x0008;
    /** {@code POLLHUP}: the device or peer hung up (output only in {@code revents}). */
    public static final int POLLHUP = 0x0010;
    /** {@code POLLNVAL}: the file descriptor is invalid or not open (output only in {@code revents}). */
    public static final int POLLNVAL = 0x0020;
}
