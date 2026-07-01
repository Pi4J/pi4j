package com.pi4j.config.exception;

/**
 * Thrown when a configuration does not contain a key that is mandatory for the requested operation.
 * The offending key name is appended to the message. This is a specialized {@link ConfigException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigMissingRequiredKeyException extends ConfigException {

    /** Message prefix to which the missing key name is appended: {@code "Configuration is missing a required key: "} */
    public static String MESSAGE =  "Configuration is missing a required key: ";

    /**
     * Creates the exception for a specific missing key.
     *
     * @param key the name of the required configuration key that was not found
     */
    public ConfigMissingRequiredKeyException(String key){
        super(MESSAGE + key);
    }

    /**
     * Creates the exception for a specific missing key, retaining an underlying cause.
     *
     * @param key   the name of the required configuration key that was not found
     * @param cause the underlying throwable that detected the missing key
     */
    public ConfigMissingRequiredKeyException(String key, Throwable cause){
        super(MESSAGE + key, cause);
    }
}
