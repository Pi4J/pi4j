#!/bin/sh

/bin/bash  ../native/i2c/build.sh
insmod i2c-mock.ko
sleep 1