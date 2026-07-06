#!/bin/sh

cd "$(dirname "$0")" || exit 1
apt-get install linux-headers-"$(uname -r)" linux-modules-extra-"$(uname -r)"
make clean
make
cp spi-mock.ko ../../resources/
make clean