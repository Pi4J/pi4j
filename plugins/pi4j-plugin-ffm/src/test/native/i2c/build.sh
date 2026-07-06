#!/bin/sh

cd "$(dirname "$0")" || exit 1
apt-get install -y linux-headers-"$(uname -r)" libi2c-dev
make clean
make
cp i2c-mock.ko ../../resources/
make clean