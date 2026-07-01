package com.pi4j.io.exception;


/**
 * Thrown when a lookup for an I/O instance by its identifier fails because no
 * matching instance is registered in the Pi4J registry. A specialization of
 * {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IONotFoundException extends IOException {

    /**
     * Creates an exception reporting that no I/O instance with the given id exists in the registry.
     *
     * @param id the identifier that was looked up but not found
     */
    public IONotFoundException(String id){
        super("IO instance [" + id + "] not found in Pi4J Registry.");
    }
}
