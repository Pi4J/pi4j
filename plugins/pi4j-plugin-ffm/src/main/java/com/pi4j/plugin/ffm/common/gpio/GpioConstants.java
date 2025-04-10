package com.pi4j.plugin.ffm.common.gpio;

/**
 * Source: NO_POSITION
 */
public class GpioConstants {
	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:23:9
	 */
	public static final int GPIO_MAX_NAME_SIZE = 32;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:45:9
	 */
	public static final int GPIO_V2_LINES_MAX = 64;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:51:9
	 */
	public static final int GPIO_V2_LINE_NUM_ATTRS_MAX = 10;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:343:9
	 */
	public static final int GPIOHANDLES_MAX = 64;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:486:9
	 */
	public static final int GPIOEVENT_EVENT_RISING_EDGE = 1;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:487:9
	 */
	public static final int GPIOEVENT_EVENT_FALLING_EDGE = 2;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:311:9
	 */
	public static final long GPIOLINE_FLAG_KERNEL = 1L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:312:9
	 */
	public static final long GPIOLINE_FLAG_IS_OUT = 2L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:313:9
	 */
	public static final long GPIOLINE_FLAG_ACTIVE_LOW = 4L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:314:9
	 */
	public static final long GPIOLINE_FLAG_OPEN_DRAIN = 8L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:315:9
	 */
	public static final long GPIOLINE_FLAG_OPEN_SOURCE = 16L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:316:9
	 */
	public static final long GPIOLINE_FLAG_BIAS_PULL_UP = 32L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:317:9
	 */
	public static final long GPIOLINE_FLAG_BIAS_PULL_DOWN = 64L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:318:9
	 */
	public static final long GPIOLINE_FLAG_BIAS_DISABLE = 128L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:378:9
	 */
	public static final long GPIOHANDLE_REQUEST_INPUT = 1L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:379:9
	 */
	public static final long GPIOHANDLE_REQUEST_OUTPUT = 2L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:380:9
	 */
	public static final long GPIOHANDLE_REQUEST_ACTIVE_LOW = 4L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:381:9
	 */
	public static final long GPIOHANDLE_REQUEST_OPEN_DRAIN = 8L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:382:9
	 */
	public static final long GPIOHANDLE_REQUEST_OPEN_SOURCE = 16L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:383:9
	 */
	public static final long GPIOHANDLE_REQUEST_BIAS_PULL_UP = 32L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:384:9
	 */
	public static final long GPIOHANDLE_REQUEST_BIAS_PULL_DOWN = 64L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:385:9
	 */
	public static final long GPIOHANDLE_REQUEST_BIAS_DISABLE = 128L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:454:9
	 */
	public static final long GPIOEVENT_REQUEST_RISING_EDGE = 1L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:455:9
	 */
	public static final long GPIOEVENT_REQUEST_FALLING_EDGE = 2L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:456:9
	 */
	public static final long GPIOEVENT_REQUEST_BOTH_EDGES = 3L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:505:9
	 */
	public static final long GPIO_GET_CHIPINFO_IOCTL = 2151986177L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:506:9
	 */
	public static final long GPIO_GET_LINEINFO_UNWATCH_IOCTL = 3221533708L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:511:9
	 */
	public static final long GPIO_V2_GET_LINEINFO_IOCTL = 3238048773L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:512:9
	 */
	public static final long GPIO_V2_GET_LINEINFO_WATCH_IOCTL = 3238048774L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:513:9
	 */
	public static final long GPIO_V2_GET_LINE_IOCTL = 3260068871L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:514:9
	 */
	public static final long GPIO_V2_LINE_SET_CONFIG_IOCTL = 3239097357L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:515:9
	 */
	public static final long GPIO_V2_LINE_GET_VALUES_IOCTL = 3222320142L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:516:9
	 */
	public static final long GPIO_V2_LINE_SET_VALUES_IOCTL = 3222320143L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:523:9
	 */
	public static final long GPIO_GET_LINEINFO_IOCTL = 3225990146L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:524:9
	 */
	public static final long GPIO_GET_LINEHANDLE_IOCTL = 3245126659L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:525:9
	 */
	public static final long GPIO_GET_LINEEVENT_IOCTL = 3224417284L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:526:9
	 */
	public static final long GPIOHANDLE_GET_LINE_VALUES_IOCTL = 3225465864L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:527:9
	 */
	public static final long GPIOHANDLE_SET_LINE_VALUES_IOCTL = 3225465865L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:528:9
	 */
	public static final long GPIOHANDLE_SET_CONFIG_IOCTL = 3226776586L;

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:529:9
	 */
	public static final long GPIO_GET_LINEINFO_WATCH_IOCTL = 3225990155L;
}
