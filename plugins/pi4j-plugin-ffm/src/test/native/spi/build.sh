#!/bin/sh

cd "$(dirname "$0")" || exit 1
apt-get install -y linux-headers-"$(uname -r)"
apt-cache show linux-modules-extra-"$(uname -r)" >/dev/null 2>&1 && apt-get install -y linux-modules-extra-"$(uname -r)"
make clean
make
cp spi-mock.ko ../../resources/
make clean