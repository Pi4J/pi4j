package com.pi4j.config.exception;

/**
 * Base unchecked exception for all configuration errors raised while building or validating a Pi4J
 * I/O configuration. Subtypes such as {@link ConfigEmptyException}, {@link ConfigMissingPrefixException}
 * and {@link ConfigMissingRequiredKeyException} signal specific configuration faults.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates a configuration exception with a descriptive message.
     *
     * @param message human-readable description of the configuration problem
     */
    public ConfigException(String message){
        super(message);
    }

    /**
     * Creates a configuration exception that wraps an underlying cause.
     *
     * @param cause the underlying throwable that triggered this configuration failure
     */
    public ConfigException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a configuration exception with a descriptive message and an underlying cause.
     *
     * @param message human-readable description of the configuration problem
     * @param cause   the underlying throwable that triggered this configuration failure
     */
    public ConfigException(String message, Throwable cause){
        super(message,cause);
    }
}
