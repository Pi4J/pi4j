/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  LinuxLibC.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */

package com.pi4j.plugin.linuxfs.internal;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * C library functions.
 *
 * @author mpilone
 * @since 10/3/24.
 */
public interface LinuxLibC extends Library {

    // This class could extend c.s.j.platform.linux.LibC, but we're not using any
    // of that functionality right now so we can avoid the jna-platform dependency
    // until we need it.

    LinuxLibC INSTANCE = LinuxLibC.LibLoader.load();

    class LibLoader {
        static LinuxLibC load() {
            return Native.load("c", LinuxLibC.class);
        }
    }

    ///////////////////////////////////
    // fcntl.h
    int O_WRONLY = 00000001;
    int O_RDWR = 00000002;
    int O_NONBLOCK = 00004000;

    ///////////////////////////////////
    // ioctl.h
    int _IOC_NRBITS = 8;
    int _IOC_TYPEBITS = 8;
    int _IOC_SIZEBITS = 14;

    int _IOC_NRSHIFT = 0;
    int _IOC_TYPESHIFT = (_IOC_NRSHIFT + _IOC_NRBITS);
    int _IOC_SIZESHIFT = (_IOC_TYPESHIFT + _IOC_TYPEBITS);
    int _IOC_DIRSHIFT = (_IOC_SIZESHIFT + _IOC_SIZEBITS);

    byte _IOC_NONE = 0;
    byte _IOC_WRITE = 1;
    byte _IOC_READ = 2;

    static int _IOC(byte dir, byte type, byte nr, int size) {
        return (((dir) << _IOC_DIRSHIFT) |
            ((type) << _IOC_TYPESHIFT) |
            ((nr) << _IOC_NRSHIFT) |
            ((size) << _IOC_SIZESHIFT));
    }

    int ioctl(int filedes, long op, Object... args);

    int open(String pathname, int flags);

    int close(int fd);
}