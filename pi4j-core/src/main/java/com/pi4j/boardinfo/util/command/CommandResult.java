package com.pi4j.boardinfo.util.command;

import com.pi4j.util.StringUtil;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable outcome of running an external command through {@link CommandExecutor}, bundling a
 * success flag together with the captured standard-output and error text. Instances are created
 * via the {@link #success(String)} and {@link #failure(String)} factory methods.
 */
public class CommandResult {
    private final boolean success;
    private final String outputMessage;
    private final String errorMessage;

    /**
     * Constructor to create a new CommandResult.
     *
     * @param success       Whether the command execution was successful.
     * @param outputMessage The standard output of the command.
     * @param errorMessage  The error message if the command failed.
     */
    private CommandResult(boolean success, String outputMessage, String errorMessage) {
        this.success = success;
        this.outputMessage = Optional.ofNullable(outputMessage).orElse(StringUtil.EMPTY);
        this.errorMessage = Optional.ofNullable(errorMessage).orElse(StringUtil.EMPTY);
    }

    /**
     * Creates a result marking a successful command execution, carrying the captured standard output.
     *
     * @param outputMessage the standard output produced by the command; {@code null} is normalized to
     *                       an empty string
     * @return a {@link CommandResult} whose {@link #isSuccess()} is {@code true} and whose error message
     *         is empty
     */
    public static CommandResult success(String outputMessage) {
        return new CommandResult(true, outputMessage, null);
    }

    /**
     * Creates a result marking a failed command execution, carrying the describing error message.
     *
     * @param errorMessage the reason the command failed, such as a timeout or exception detail;
     *                      {@code null} is normalized to an empty string
     * @return a {@link CommandResult} whose {@link #isSuccess()} is {@code false} and whose output
     *         message is empty
     */
    public static CommandResult failure(String errorMessage) {
        return new CommandResult(false, null, errorMessage);
    }

    /**
     * Returns whether the command execution was successful.
     *
     * @return {@code true} if successful, {@code false} otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the standard output message from the command.
     *
     * @return The standard output message.
     */
    public String getOutputMessage() {
        return outputMessage;
    }

    /**
     * Returns the error message from the command execution.
     *
     * @return The error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return String.format("CommandResult{success=%b, outputMessage='%s', errorMessage='%s'}",
                success, outputMessage, errorMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandResult that = (CommandResult) o;
        return success == that.success &&
                Objects.equals(outputMessage, that.outputMessage) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, outputMessage, errorMessage);
    }
}
