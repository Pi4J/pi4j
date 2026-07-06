package com.pi4j.util;


/**
 * Utility for converting between frequencies (in hertz) and time periods, and between hertz and its kilo/mega
 * multiples. Used across Pi4J to translate configured frequencies (for example PWM or SPI clock rates) into the
 * period values expected by lower-level I/O.
 */
public class Frequency {

    /** Number of hertz in one megahertz. */
    public static final long MEGAHERTZ = 1000000;
    /** Number of hertz in one kilohertz. */
    public static final long KILOHERTZ = 1000;
    /** Base unit; one hertz. */
    public static final long HERTZ = 1;

    /**
     * Convert Kilohertz to Hertz
     *
     * @param frequency number of kilohertz
     * @return total number of hertz
     */
    public static int kilohertz(Number frequency){
        return Math.round(frequency.floatValue() * KILOHERTZ);
    }

    /**
     * Convert Megahertz to Hertz
     *
     * @param frequency number of megahertz
     * @return total number of hertz
     */
    public static int megahertz(Number frequency){
        return Math.round(frequency.floatValue() * MEGAHERTZ);
    }


    /**
     * Convert Frequency (in Hertz) to Nanoseconds
     *
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
     *
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
     *
     * @param frequency value in hertz
     * @return total number of milliseconds represented by this frequency value
     */
    public static float milliseconds(Number frequency){
        float period = 1000; // MILLISECONDS PER SECOND;
        period = period / frequency.longValue();
        return period;
    }

    /**
     * Computes the frequency (in hertz) corresponding to a given period expressed in nanoseconds.
     *
     * @param nanoseconds the period length in nanoseconds
     * @return the frequency in hertz, or {@code 0} if the period is zero or negative
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

