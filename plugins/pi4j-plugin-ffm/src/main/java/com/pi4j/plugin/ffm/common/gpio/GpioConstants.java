package com.pi4j.plugin.ffm.common.gpio;

/**
 * Numeric values of the kernel GPIO character-device constants from {@code <uapi/linux/gpio.h>} used by this
 * native backend. They include size limits, line/event flag bits and the {@code ioctl} request numbers issued
 * against {@code /dev/gpiochipN} file descriptors. Values are taken from a Linux 6.8 header; the {@code ioctl}
 * numbers are encoded for the kernel's {@code _IOR}/{@code _IOWR} scheme and are 64-bit safe.
 */
public class GpioConstants {
	/** {@code GPIO_MAX_NAME_SIZE}: maximum length, in bytes, of a GPIO chip or line name (including NUL). */
	public static final int GPIO_MAX_NAME_SIZE = 32;

	/** {@code GPIO_V2_LINES_MAX}: maximum number of lines that can be requested in a single v2 line request. */
	public static final int GPIO_V2_LINES_MAX = 64;

	/** {@code GPIO_V2_LINE_NUM_ATTRS_MAX}: maximum number of configuration attributes attachable to a v2 line request. */
	public static final int GPIO_V2_LINE_NUM_ATTRS_MAX = 10;

	/** {@code GPIOHANDLES_MAX}: maximum number of lines in a (legacy v1) handle request. */
	public static final int GPIOHANDLES_MAX = 64;

	/** {@code GPIOEVENT_EVENT_RISING_EDGE}: event identifier reported for a rising (inactive-to-active) edge. */
	public static final int GPIOEVENT_EVENT_RISING_EDGE = 1;

	/** {@code GPIOEVENT_EVENT_FALLING_EDGE}: event identifier reported for a falling (active-to-inactive) edge. */
	public static final int GPIOEVENT_EVENT_FALLING_EDGE = 2;

	/** {@code GPIOLINE_FLAG_KERNEL}: line is already in use by the kernel and not available for request. */
	public static final long GPIOLINE_FLAG_KERNEL = 1L;

	/** {@code GPIOLINE_FLAG_IS_OUT}: line is configured as an output. */
	public static final long GPIOLINE_FLAG_IS_OUT = 2L;

	/** {@code GPIOLINE_FLAG_ACTIVE_LOW}: line's active state is physical low. */
	public static final long GPIOLINE_FLAG_ACTIVE_LOW = 4L;

	/** {@code GPIOLINE_FLAG_OPEN_DRAIN}: line is an open-drain output. */
	public static final long GPIOLINE_FLAG_OPEN_DRAIN = 8L;

	/** {@code GPIOLINE_FLAG_OPEN_SOURCE}: line is an open-source output. */
	public static final long GPIOLINE_FLAG_OPEN_SOURCE = 16L;

	/** {@code GPIOLINE_FLAG_BIAS_PULL_UP}: line has pull-up bias enabled. */
	public static final long GPIOLINE_FLAG_BIAS_PULL_UP = 32L;

	/** {@code GPIOLINE_FLAG_BIAS_PULL_DOWN}: line has pull-down bias enabled. */
	public static final long GPIOLINE_FLAG_BIAS_PULL_DOWN = 64L;

	/** {@code GPIOLINE_FLAG_BIAS_DISABLE}: line has bias explicitly disabled. */
	public static final long GPIOLINE_FLAG_BIAS_DISABLE = 128L;

	/** {@code GPIOHANDLE_REQUEST_INPUT}: request the line as an input. */
	public static final long GPIOHANDLE_REQUEST_INPUT = 1L;

	/** {@code GPIOHANDLE_REQUEST_OUTPUT}: request the line as an output. */
	public static final long GPIOHANDLE_REQUEST_OUTPUT = 2L;

	/** {@code GPIOHANDLE_REQUEST_ACTIVE_LOW}: request the line with active-low polarity. */
	public static final long GPIOHANDLE_REQUEST_ACTIVE_LOW = 4L;

	/** {@code GPIOHANDLE_REQUEST_OPEN_DRAIN}: request the line as an open-drain output. */
	public static final long GPIOHANDLE_REQUEST_OPEN_DRAIN = 8L;

	/** {@code GPIOHANDLE_REQUEST_OPEN_SOURCE}: request the line as an open-source output. */
	public static final long GPIOHANDLE_REQUEST_OPEN_SOURCE = 16L;

	/** {@code GPIOHANDLE_REQUEST_BIAS_PULL_UP}: request the line with pull-up bias. */
	public static final long GPIOHANDLE_REQUEST_BIAS_PULL_UP = 32L;

	/** {@code GPIOHANDLE_REQUEST_BIAS_PULL_DOWN}: request the line with pull-down bias. */
	public static final long GPIOHANDLE_REQUEST_BIAS_PULL_DOWN = 64L;

	/** {@code GPIOHANDLE_REQUEST_BIAS_DISABLE}: request the line with bias disabled. */
	public static final long GPIOHANDLE_REQUEST_BIAS_DISABLE = 128L;

	/** {@code GPIOEVENT_REQUEST_RISING_EDGE}: request edge detection on rising edges only. */
	public static final long GPIOEVENT_REQUEST_RISING_EDGE = 1L;

	/** {@code GPIOEVENT_REQUEST_FALLING_EDGE}: request edge detection on falling edges only. */
	public static final long GPIOEVENT_REQUEST_FALLING_EDGE = 2L;

	/** {@code GPIOEVENT_REQUEST_BOTH_EDGES}: request edge detection on both rising and falling edges. */
	public static final long GPIOEVENT_REQUEST_BOTH_EDGES = 3L;

	/** {@code GPIO_GET_CHIPINFO_IOCTL}: {@code ioctl} request to read {@code struct gpiochip_info} for a chip. */
	public static final long GPIO_GET_CHIPINFO_IOCTL = 2151986177L;

	/** {@code GPIO_GET_LINEINFO_UNWATCH_IOCTL}: {@code ioctl} request to stop watching a line for info changes. */
	public static final long GPIO_GET_LINEINFO_UNWATCH_IOCTL = 3221533708L;

	/** {@code GPIO_V2_GET_LINEINFO_IOCTL}: {@code ioctl} request to read v2 {@code struct gpio_v2_line_info} for a line. */
	public static final long GPIO_V2_GET_LINEINFO_IOCTL = 3238048773L;

	/** {@code GPIO_V2_GET_LINEINFO_WATCH_IOCTL}: {@code ioctl} request to read v2 line info and watch the line for changes. */
	public static final long GPIO_V2_GET_LINEINFO_WATCH_IOCTL = 3238048774L;

	/** {@code GPIO_V2_GET_LINE_IOCTL}: {@code ioctl} request to request one or more lines (v2 {@code struct gpio_v2_line_request}). */
	public static final long GPIO_V2_GET_LINE_IOCTL = 3260068871L;

	/** {@code GPIO_V2_LINE_SET_CONFIG_IOCTL}: {@code ioctl} request to update the configuration of requested v2 lines. */
	public static final long GPIO_V2_LINE_SET_CONFIG_IOCTL = 3239097357L;

	/** {@code GPIO_V2_LINE_GET_VALUES_IOCTL}: {@code ioctl} request to read the current values of requested v2 lines. */
	public static final long GPIO_V2_LINE_GET_VALUES_IOCTL = 3222320142L;

	/** {@code GPIO_V2_LINE_SET_VALUES_IOCTL}: {@code ioctl} request to set the output values of requested v2 lines. */
	public static final long GPIO_V2_LINE_SET_VALUES_IOCTL = 3222320143L;

	/** {@code GPIO_GET_LINEINFO_IOCTL}: legacy v1 {@code ioctl} request to read {@code struct gpioline_info} for a line. */
	public static final long GPIO_GET_LINEINFO_IOCTL = 3225990146L;

	/** {@code GPIO_GET_LINEHANDLE_IOCTL}: legacy v1 {@code ioctl} request to request a line handle. */
	public static final long GPIO_GET_LINEHANDLE_IOCTL = 3245126659L;

	/** {@code GPIO_GET_LINEEVENT_IOCTL}: legacy v1 {@code ioctl} request to request a line event file descriptor. */
	public static final long GPIO_GET_LINEEVENT_IOCTL = 3224417284L;

	/** {@code GPIOHANDLE_GET_LINE_VALUES_IOCTL}: legacy v1 {@code ioctl} request to read values from a line handle. */
	public static final long GPIOHANDLE_GET_LINE_VALUES_IOCTL = 3225465864L;

	/** {@code GPIOHANDLE_SET_LINE_VALUES_IOCTL}: legacy v1 {@code ioctl} request to set values on a line handle. */
	public static final long GPIOHANDLE_SET_LINE_VALUES_IOCTL = 3225465865L;

	/** {@code GPIOHANDLE_SET_CONFIG_IOCTL}: legacy v1 {@code ioctl} request to update the configuration of a line handle. */
	public static final long GPIOHANDLE_SET_CONFIG_IOCTL = 3226776586L;

	/** {@code GPIO_GET_LINEINFO_WATCH_IOCTL}: legacy v1 {@code ioctl} request to read line info and watch the line for changes. */
	public static final long GPIO_GET_LINEINFO_WATCH_IOCTL = 3225990155L;
}
