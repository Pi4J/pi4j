#!/bin/sh

cd "$(dirname "$0")" || exit 1
make clean
make
cp spi-mock.ko ../../resources/
make clean