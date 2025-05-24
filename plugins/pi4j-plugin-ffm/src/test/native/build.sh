#!/bin/sh

make clean
make
mv spi-mock.ko ../resources
make clean