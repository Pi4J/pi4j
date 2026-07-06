package com.pi4j.config.exception;

/**
 * Thrown when a configuration is required but is missing or contains no entries.
 * This is a specialized {@link ConfigException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigEmptyException extends ConfigException {

    /** Detail message used for this exception: {@code "Configuration is missing or empty."} */
    public static String MESSAGE =  "Configuration is missing or empty.";

    /**
     * Creates the exception with the default {@link #MESSAGE}.
     */
    public ConfigEmptyException(){
        super(MESSAGE);
    }

    /**
     * Creates the exception with the default {@link #MESSAGE} and an underlying cause.
     *
     * @param cause the underlying throwable that detected the missing or empty configuration
     */
    public ConfigEmptyException(Throwable cause){
        super(MESSAGE, cause);
    }
}
