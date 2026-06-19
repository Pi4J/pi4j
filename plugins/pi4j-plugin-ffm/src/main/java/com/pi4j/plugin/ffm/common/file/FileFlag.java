package com.pi4j.plugin.ffm.common.file;

/**
 * Numeric values of the glibc {@code <fcntl.h>}/{@code <sys/file.h>}/{@code <unistd.h>} flag constants used when
 * calling {@code open}, {@code flock} and {@code access} through the Foreign Function &amp; Memory API.
 * These mirror the {@code O_*}, {@code LOCK_*} and {@code *_OK} macros as defined for Linux, so that device nodes
 * such as {@code /dev/gpiochipN}, {@code /dev/i2c-N} and {@code /dev/spidevN.N} can be opened with the right mode.
 */
public final class FileFlag {

    // open64 flags
    /** {@code O_APPEND}: write operations append to the end of the file. */
    public static final int O_APPEND = 1024;
    /** {@code O_ASYNC}: generate a signal when input or output becomes possible on the file descriptor. */
    public static final int O_ASYNC = 8192;
    /** {@code O_CLOEXEC}: close the file descriptor automatically on {@code execve}. */
    public static final int O_CLOEXEC = 524288;
    /** {@code O_CREAT}: create the file if it does not already exist. */
    public static final int O_CREAT = 64;
    /** {@code O_DIRECT}: minimize cache effects of I/O by transferring directly to/from user space. */
    public static final int O_DIRECT = 16384;
    /** {@code O_DIRECTORY}: fail unless the opened path is a directory. */
    public static final int O_DIRECTORY = 65536;
    /** {@code O_DSYNC}: write operations complete according to synchronized I/O data integrity. */
    public static final int O_DSYNC = 4096;
    /** {@code O_EXCL}: in combination with {@link #O_CREAT}, fail if the file already exists. */
    public static final int O_EXCL = 128;
    /** {@code O_LARGEFILE}: allow files whose size cannot be represented in 31 bits (a no-op on 64-bit Linux). */
    public static final int O_LARGEFILE = 0;
    /** {@code O_NOATIME}: do not update the file's last access time on read. */
    public static final int O_NOATIME = 262144;
    /** {@code O_NONBLOCK}: open in non-blocking mode so reads/writes do not stall; required for {@code poll}-driven GPIO event handling. */
    public static final int O_NONBLOCK = 2048;
    /** {@code O_NOCTTY}: do not make the opened terminal the controlling terminal of the process. */
    public static final int O_NOCTTY = 256;
    /** {@code O_NOFOLLOW}: fail if the trailing component of the path is a symbolic link. */
    public static final int O_NOFOLLOW = 131072;
    /** {@code O_PATH}: open only to obtain a file descriptor referencing the location, without read/write access. */
    public static final int O_PATH = 2097152;
    /** {@code O_RDONLY}: open the file for reading only. */
    public static final int O_RDONLY = 0;
    /** {@code O_RDWR}: open the file for both reading and writing. */
    public static final int O_RDWR = 2;
    /** {@code O_SYNC}: write operations complete according to synchronized I/O file integrity. */
    public static final int O_SYNC = 1052672;
    /** {@code O_TMPFILE}: create an unnamed temporary regular file in the given directory. */
    public static final int O_TMPFILE = 4259840;
    /** {@code O_TRUNC}: truncate an existing regular file to length zero on open. */
    public static final int O_TRUNC = 512;
    /** {@code O_WRONLY}: open the file for writing only. */
    public static final int O_WRONLY = 1;

    // flock flags
    /** {@code LOCK_EX}: place an exclusive advisory lock on the file. */
    public static final int LOCK_EX = 2;
    /** {@code LOCK_UN}: remove an existing advisory lock from the file. */
    public static final int LOCK_UN = 8;

    // access flags
    /** {@code F_OK}: test for the existence of the file. */
    public static final int F_OK = 0;
    /** {@code R_OK}: test for read permission on the file. */
    public static final int R_OK = 0x04;
    /** {@code W_OK}: test for write permission on the file. */
    public static final int W_OK = 0x02;
}
