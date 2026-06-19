package com.pi4j.io.spi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiMode.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public enum SpiMode {
    MODE_0(0), MODE_1(1), MODE_2(2), MODE_3(3);

    private final int mode;

    private SpiMode(int mode) {
        this.mode = mode;
    }

    /**
     * <p>Getter for the field <code>mode</code>.</p>
     */
    public int getMode() {
        return mode;
    }

    public static SpiMode getByNumber(short modeNumber){
        return getByNumber((int)modeNumber);
    }

    public static SpiMode getByNumber(int modeNumber){
        for(var item : SpiMode.values()){
            if(item.getMode() == modeNumber){
                return item;
            }
        }
        return null;
    }

    public static SpiMode parse(String mode) {
        if(mode.equalsIgnoreCase("0")) return SpiMode.MODE_0;
        if(mode.equalsIgnoreCase("1")) return SpiMode.MODE_1;
        if(mode.equalsIgnoreCase("2")) return SpiMode.MODE_2;
        if(mode.equalsIgnoreCase("3")) return SpiMode.MODE_3;
        return Spi.DEFAULT_MODE;
    }
}
