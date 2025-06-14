#!/bin/sh

cd src/test/native/ || exit 1
make clean
make
mv spi-mock.ko ../resources
make clean