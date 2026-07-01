package com.pi4j.io.exception;

/**
 * Thrown when an I/O value falls outside its permitted range, for example an
 * out-of-range pin number, channel, or buffer index. A specialization of
 * {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOBoundsException extends IOException {

    /**
     * Creates an exception describing a value that lies outside the allowed inclusive range.
     *
     * @param value the offending value that was rejected
     * @param min   the lowest permitted value (inclusive)
     * @param max   the highest permitted value (inclusive)
     */
    public IOBoundsException(Integer value, Integer min, Integer max){
        super("The requested value ["
            + value
            + "] is out of bounds; <min: "
            + min
            + ", max: "
            + max
            + ">");
    }
}
