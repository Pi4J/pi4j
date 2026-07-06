package com.pi4j.config.exception;

/**
 * Thrown when a required {@code prefix} argument used to resolve configuration keys is {@code null}
 * or missing. This is a specialized {@link ConfigException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigMissingPrefixException extends ConfigException {

    /** Detail message used for this exception: {@code "A required 'prefix' argument is null or missing."} */
    public static String MESSAGE =  "A required 'prefix' argument is null or missing.";

    /**
     * Creates the exception with the default {@link #MESSAGE}.
     */
    public ConfigMissingPrefixException(){
        super(MESSAGE);
    }

    /**
     * Creates the exception with the default {@link #MESSAGE} and an underlying cause.
     *
     * @param cause the underlying throwable that detected the missing prefix
     */
    public ConfigMissingPrefixException(Throwable cause){
        super(MESSAGE, cause);
    }
}
