package com.pi4j.util;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DecimalFormatter.java
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Utility for rendering numeric values as locale-independent decimal strings, suppressing trailing zeros and
 * grouping separators. Used within Pi4J to format measured or computed values for display and logging.
 */
public class DecimalFormatter {

    /** Locale-default symbols (decimal separator, etc.) shared by the formatter. */
    protected static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
    /** Formatter that prints up to eighteen fractional digits with no grouping and no trailing zeros. */
    protected static DecimalFormat df = new DecimalFormat("#.##################", otherSymbols);

    /**
     * Formats the given number as a plain decimal string, omitting any trailing zeros and grouping separators.
     *
     * @param value the value to format
     * @return the decimal string representation of the value
     */
    public static String format(Number value){
        return df.format(value);
    }
}
