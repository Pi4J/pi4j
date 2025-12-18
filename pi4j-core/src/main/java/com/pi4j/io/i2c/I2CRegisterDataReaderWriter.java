package com.pi4j.io.i2c;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CRegisterDataReaderWriter.java
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

/**
 * I2C Register Data Writer Interface for Pi4J Data Communications
 *
 * @author Robert Savage
 * <p>
 * Based on previous contributions from:
 *        Daniel Sendula,
 *        <a href="http://raspelikan.blogspot.co.at">RasPelikan</a>
 * @version $Id: $Id
 */
public interface I2CRegisterDataReaderWriter extends I2CRegisterDataReader, I2CRegisterDataWriter {
    /**
     * Write a single word value (16-bit) to the I2C device register
     * and immediately reads back a 16-bit word value.
     *
     * @param register the register address to write to
     * @param word 16-bit word value to be written
     * @return The 16-bit word value read/returned; or a negative value if error
     */
    default int writeReadRegisterWord(int register, int word) {
        writeRegisterWord(register, word);
        return readRegisterWord(register);
    }
}
