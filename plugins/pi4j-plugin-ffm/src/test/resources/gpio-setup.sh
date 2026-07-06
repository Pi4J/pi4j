#!/bin/bash

# Mock GPIO chip layout is customizable through environment variables so tests
# can shape the chips without editing the driver. Each is a comma separated list
# where the number of entries equals the number of chips to create:
#   GPIO_MOCK_NGPIOS  - lines per chip            (default "8,1")
#   GPIO_MOCK_LABELS  - label per chip            (default "accessible,inaccessible")
#   GPIO_MOCK_HOGS    - occupied line per chip,   (default "2,-1", -1 means none)
#   GPIO_MOCK_NUMBERS - desired /dev/gpiochip<N>  (optional, e.g. "0,1,99")
# Example: GPIO_MOCK_NGPIOS=16,4 GPIO_MOCK_LABELS=bankA,bankB GPIO_MOCK_HOGS=-1,-1 ./gpio-setup.sh
NGPIOS="${GPIO_MOCK_NGPIOS:-8,1}"
LABELS="${GPIO_MOCK_LABELS:-accessible,inaccessible}"
HOGS="${GPIO_MOCK_HOGS:-2,-1}"
NUMBERS="${GPIO_MOCK_NUMBERS:-97,98}"
# Set to 1 to enable verbose debug logging from the mock driver (visible in dmesg)
DEBUG="${GPIO_MOCK_DEBUG:-0}"

/bin/bash  ../native/gpio/build.sh
insmod gpio-mock.ko ngpios="$NGPIOS" labels="$LABELS" hog_lines="$HOGS" debug="$DEBUG"

# The kernel assigns /dev/gpiochip<N> numbers automatically (always the lowest
# free integer) - a GPIO driver cannot request a specific one the way i2c can.
# To expose a chip under a fixed number, we symlink it to the requested
# /dev/gpiochip<N>. The mock's chips are children of its platform device in sysfs
# and the kernel numbers them consecutively in registration order, which matches
# the order of the labels/ngpios/NUMBERS lists - so we map them by position,
# without needing the libgpiod 'gpiodetect' tool.
if [ -n "$NUMBERS" ]; then
	i=1
	for chip in $(ls -d /sys/devices/platform/gpio-mock/gpiochip* 2>/dev/null | sort -V); do
		real="${chip##*/}"  # gpiochip<N> as assigned by the kernel
		number=$(echo "$NUMBERS" | cut -d',' -f"$i")
		i=$((i + 1))
		[ -z "$number" ] && continue
		# only create a symlink if the chip is not already under the wanted number
		if [ "$real" != "gpiochip$number" ]; then
			ln -sf "/dev/$real" "/dev/gpiochip$number"
		fi
	done
fi

# Sleep a second to let udev rules to be applied
sleep 0.5