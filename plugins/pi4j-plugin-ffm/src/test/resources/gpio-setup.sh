#!/bin/sh

modprobe configfs
mountpoint /sys/kernel/config > /dev/null || mount -t configfs configfs /sys/kernel/config
modprobe gpio-sim

mkdir /sys/kernel/config/gpio-sim/basic

mkdir /sys/kernel/config/gpio-sim/basic/bank0
echo "accessible" > /sys/kernel/config/gpio-sim/basic/bank0/label
echo 8 > /sys/kernel/config/gpio-sim/basic/bank0/num_lines
mkdir -p /sys/kernel/config/gpio-sim/basic/bank0/line2/hog
echo "occupied" > /sys/kernel/config/gpio-sim/basic/bank0/line2/hog/name
echo "output-high" > /sys/kernel/config/gpio-sim/basic/bank0/line2/hog/direction

mkdir /sys/kernel/config/gpio-sim/basic/bank1
echo "inaccessible" > /sys/kernel/config/gpio-sim/basic/bank1/label
echo 1 > /sys/kernel/config/gpio-sim/basic/bank1/num_lines

echo 1 > /sys/kernel/config/gpio-sim/basic/live

chmod 0660 /dev/gpiochip0
chown root:dialout /dev/gpiochip0