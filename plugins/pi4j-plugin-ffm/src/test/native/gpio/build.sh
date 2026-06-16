#!/bin/sh

cd "$(dirname "$0")" || exit 1
make clean
make
cp gpio-mock.ko ../../resources/
make clean
