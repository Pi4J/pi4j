package com.pi4j.util;

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
