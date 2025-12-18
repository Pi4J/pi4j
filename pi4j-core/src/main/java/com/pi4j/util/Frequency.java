package com.pi4j.util;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Frequency.java
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
 * <p>Frequency class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class Frequency {

    public static final long MEGAHERTZ = 1000000;
    public static final long KILOHERTZ = 1000;
    public static final long HERTZ = 1;

    /**
     * Convert Kilohertz to Hertz
     * @param frequency number of kilohertz
     * @return total number of hertz
     */
    public static int kilohertz(Number frequency){
        return Math.round(frequency.floatValue() * KILOHERTZ);
    }

    /**
     * Convert Megahertz to Hertz
     * @param frequency number of megahertz
     * @return total number of hertz
     */
    public static int megahertz(Number frequency){
        return Math.round(frequency.floatValue() * MEGAHERTZ);
    }


    /**
     * Convert Frequency (in Hertz) to Nanoseconds
     * @param frequency value in hertz
     * @return total number of nanoseconds represented by this frequency value
     */
    public static long nanoseconds(Number frequency){
        long period = 1000000000; // NANOSECONDS PER SECOND;
        period = period / frequency.longValue();
        return period;
    }

    /**
     * Convert Frequency (in Hertz) to Microseconds
     * @param frequency value in hertz
     * @return total number of microseconds represented by this frequency value
     */
    public static long microseconds(Number frequency){
        long period = 1000000; // MICROSECONDS PER SECOND;
        period = period / frequency.longValue();
        return period;
    }

    /**
     * Convert Frequency (in Hertz) to Milliseconds
     * @param frequency value in hertz
     * @return total number of milliseconds represented by this frequency value
     */
    public static float milliseconds(Number frequency){
        float period = 1000; // MILLISECONDS PER SECOND;
        period = period / frequency.longValue();
        return period;
    }

    /**
     * Get Frequency (in Hertz) from Nanoseconds
     * @param nanoseconds value in hertz
     * @return total number of nanoseconds represented by this frequency value
     */
    public static int getFrequencyFromNanos(Number nanoseconds){
        int frequency;
        if(nanoseconds.longValue() <= 0){
            return 0;
        }
        long period = 1_000_000_000; // NANOSECONDS PER SECOND;
        frequency = (int) Math.round(period / (nanoseconds.longValue() * 1.0));
        return frequency;
    }
}

