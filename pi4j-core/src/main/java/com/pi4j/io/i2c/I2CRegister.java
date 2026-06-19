package com.pi4j.io.i2c;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CRegister.java
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

import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;

/**
 * I2C Device Register.
 * This abstraction allows data to be read or written to a specific device register on the I2C bus.
 */
public interface I2CRegister extends IODataWriter, IODataReader, I2CRegisterDataReader, I2CRegisterDataWriter {
    /**
     * @return This I2C device register address
     */
    int getAddress();
    default int address(){
        return getAddress();
    }

    /**
     * Write a single word value (16-bit) to the I2C device register.
     *
     * @param word 16-bit word value to be written
     */
    void writeWord(int word);

    /**
     * Read a single word value (16-bit) to the I2C device register.
     *
     * @return If success, then returns 16-bit word value read from I2C register; else a negative error code.
     */
    int readWord();

    /**
     * Write a single word value (16-bit) to the I2C device register
     * and immediately reads back a 16-bit word value.
     *
     * @param word 16-bit word value to be written
     * @return The 16-bit word value read/returned; or a negative value if error
     */
    int writeReadWord(int word);
}
