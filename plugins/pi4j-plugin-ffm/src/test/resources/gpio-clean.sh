#!/bin/sh
# SPDX-FileCopyrightText: 2023 Kent Gibson <warthog618@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0 OR MIT

# A helper to remove any orphaned gpio-sims from the system.
# This should only be necessary if a test was killed abnormally
# preventing it from cleaning up the sims it created, or if you
# created a sim using basic_sim.sh.

SIMDIR="/sys/kernel/config/gpio-sim"
find $SIMDIR -type d -name hog -exec rmdir '{}' '+'
find $SIMDIR -type d -name "line*" -exec rmdir '{}' '+'
find $SIMDIR -type d -name "bank*" -exec rmdir '{}' '+'
rmdir $SIMDIR/*
