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
NUMBERS="${GPIO_MOCK_NUMBERS:-}"

/bin/bash  ../native/gpio/build.sh
insmod gpio-mock.ko ngpios="$NGPIOS" labels="$LABELS" hog_lines="$HOGS"

# The kernel assigns /dev/gpiochip<N> numbers automatically (always the lowest
# free integer) - a GPIO driver cannot request a specific one the way i2c can.
# To expose a chip under a fixed number, we locate it by its label (which the
# driver lets us set) and symlink it to the requested /dev/gpiochip<N>.
if [ -n "$NUMBERS" ]; then
	i=1
	for number in $(echo "$NUMBERS" | tr ',' ' '); do
		label=$(echo "$LABELS" | cut -d',' -f"$i")
		i=$((i + 1))
		[ -z "$label" ] && continue
		# resolve the real /dev node of the chip carrying this label
		real=$(gpiodetect 2>/dev/null | awk -v l="[$label]" '$2 == l { print $1; exit }')
		if [ -z "$real" ]; then
			echo "gpio-mock: could not find chip labelled '$label' to map to gpiochip$number" >&2
			continue
		fi
		# only create a symlink if the chip is not already under the wanted number
		if [ "$real" != "gpiochip$number" ]; then
			ln -sf "/dev/$real" "/dev/gpiochip$number"
		fi
	done
fi

# Sleep a second to let udev rules to be applied
sleep 0.5