#!/bin/sh

# Remove any fixed-number symlinks created by gpio-setup.sh. Real gpiochip nodes
# are character devices, never symlinks, so this only touches what we added.
for chip in /dev/gpiochip*; do
	[ -L "$chip" ] && rm -f "$chip"
done

rmmod gpio_mock
