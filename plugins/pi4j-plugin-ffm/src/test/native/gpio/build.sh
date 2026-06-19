#!/bin/sh

cd "$(dirname "$0")" || exit 1
apt-get install -y linux-headers-"$(uname -r)"
make clean
make
cp gpio-mock.ko ../../resources/
make clean
