#!/bin/sh

cd "$(dirname "$0")" || exit 1
make clean
make
cp i2c-mock.ko ../../resources/
make clean