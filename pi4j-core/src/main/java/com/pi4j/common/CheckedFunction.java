package com.pi4j.common;

/**
 * A function that accepts one argument and produces a result, like {@link java.util.function.Function},
 * but whose {@link #apply(Object)} method is permitted to throw a checked {@link Exception}. This lets Pi4J
 * pass exception-throwing lambdas (for example I/O operations) into functional pipelines without wrapping
 * every checked exception.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    /**
     * Applies this function to the given argument.
     *
     * @param t the input argument to operate on
     * @return the result produced from the input
     * @throws Exception if the function cannot produce a result
     */
    R apply(T t) throws Exception;
}