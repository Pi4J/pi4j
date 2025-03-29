package com.pi4j.plugin.ffm.common.poll;

/**
 * Flags for calling linux poll.
 * @see <a href="https://elixir.bootlin.com/linux/latest/source/include/uapi/asm-generic/poll.h#L6">linux sources</a>
 */
public final class PollFlag {

    public static final int POLLIN	= 0x0001;
    public static final int POLLERR	= 0x0008;
}
