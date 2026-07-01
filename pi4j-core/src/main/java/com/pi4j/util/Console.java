package com.pi4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience helper used by Pi4J examples and applications to produce formatted, decorated text output
 * (titles, boxes, separator lines, screen control) via SLF4J. All write methods route through the logger and
 * return this instance for fluent chaining. Layout helpers delegate to {@link StringUtil}.
 */
public class Console {

    private static final Logger logger = LoggerFactory.getLogger(Console.class);

    private static final int LINE_WIDTH = 60;
    /** ANSI escape sequence that clears the terminal screen and moves the cursor to the home position. */
    public static final String CLEAR_SCREEN_ESCAPE_SEQUENCE = "\033[2J\033[1;1H";
    /** ANSI escape sequence that erases the current terminal line. */
    public static final String ERASE_LINE_ESCAPE_SEQUENCE = "\033[K";

    /** Character used to draw separator lines and title borders. */
    public static final char LINE_SEPARATOR_CHAR = '*';
    /** A full-width separator line composed of {@link #LINE_SEPARATOR_CHAR} characters. */
    public static final String LINE_SEPARATOR = StringUtil.repeat(LINE_SEPARATOR_CHAR, LINE_WIDTH);

    protected boolean exiting = false;

    /**
     * Formats the given arguments and prints the result followed by a line break.
     *
     * @param format a {@link String#format(String, Object...)} format string
     * @param args   the arguments referenced by the format specifiers
     * @return this instance for method chaining
     */
    public synchronized Console println(String format, Object ... args){
        return println(String.format(format, args));
    }

    /**
     * Formats the given arguments and prints the result without a trailing line break.
     *
     * @param format a {@link String#format(String, Object...)} format string
     * @param args   the arguments referenced by the format specifiers
     * @return this instance for method chaining
     */
    public synchronized Console print(String format, Object ... args){
        return print(String.format(format, args));
    }

    /**
     * Prints a single line of text.
     *
     * @param line the text to print
     * @return this instance for method chaining
     */
    public synchronized Console println(String line){
        logger.info(line);
        return this;
    }

    /**
     * Prints the {@link Object#toString()} representation of the given object as a single line.
     *
     * @param line the object whose string representation is printed
     * @return this instance for method chaining
     */
    public synchronized Console println(Object line){
        logger.info(line.toString());
        return this;
    }

    /**
     * Prints an empty line.
     *
     * @return this instance for method chaining
     */
    public synchronized Console println(){
        return println("");
    }

    /**
     * Prints the {@link Object#toString()} representation of the given object.
     *
     * @param data the object whose string representation is printed
     * @return this instance for method chaining
     */
    public synchronized Console print(Object data){
        logger.info(data.toString());
        return this;
    }

    /**
     * Prints the given text.
     *
     * @param data the text to print
     * @return this instance for method chaining
     */
    public synchronized Console print(String data){
        logger.info(data);
        return this;
    }

    /**
     * Prints a line consisting of the given character repeated a number of times.
     *
     * @param character the character to repeat
     * @param repeat    the number of times to repeat the character
     * @return this instance for method chaining
     */
    public synchronized Console println(char character, int repeat){
        return println(StringUtil.repeat(character, repeat));
    }

    /**
     * Prints a single empty line.
     *
     * @return this instance for method chaining
     */
    public synchronized Console emptyLine(){
        return emptyLine(1);
    }

    /**
     * Prints the requested number of empty lines.
     *
     * @param number the number of empty lines to print
     * @return this instance for method chaining
     */
    public synchronized Console emptyLine(int number){
        for(var index = 0; index < number; index++){
            println();
        }
        return this;
    }

    /**
     * Prints a full-width separator line using the default {@link #LINE_SEPARATOR}.
     *
     * @return this instance for method chaining
     */
    public synchronized Console separatorLine(){
        return println(LINE_SEPARATOR);
    }

    /**
     * Prints a full-width separator line composed of the given character.
     *
     * @param character the character used to draw the line
     * @return this instance for method chaining
     */
    public synchronized Console separatorLine(char character){
        return separatorLine(character, LINE_WIDTH);
    }

    /**
     * Prints a separator line of the given length composed of the given character.
     *
     * @param character the character used to draw the line
     * @param length    the number of characters in the line
     * @return this instance for method chaining
     */
    public synchronized Console separatorLine(char character, int length){
        return println(StringUtil.repeat(character, length));
    }

    /**
     * Clears the screen and prints the given lines centered between double separator borders, producing a
     * banner-style title block.
     *
     * @param title one or more lines of title text, each centered on its own line
     * @return this instance for method chaining
     */
    public synchronized Console title(String ... title){
        clearScreen().separatorLine().separatorLine().emptyLine();
        for(var s : title) {
            println(StringUtil.center(s, LINE_WIDTH));
        }
        emptyLine().separatorLine().separatorLine().emptyLine();
        return this;
    }

    /**
     * Prints the given lines enclosed in a bordered box using the default padding.
     *
     * @param lines the lines of text to render inside the box
     * @return this instance for method chaining
     */
    public synchronized Console box(String ... lines) {
        return box(2, lines);
    }

    /**
     * Prints the given lines enclosed in a bordered box with the requested horizontal padding around the text.
     *
     * @param padding the number of padding characters between the box border and the text on each side
     * @param lines   the lines of text to render inside the box
     * @return this instance for method chaining
     */
    public synchronized Console box(int padding, String ... lines) {
        int max_length = 0;
        for(var l : lines) {
            if (l.length() > max_length) {
                max_length = l.length();
            }
        }
        separatorLine('-', max_length + padding * 2 + 2);
        var left  = StringUtil.padRight("|", padding);
        var right = StringUtil.padLeft("|", padding);
        for(var l : lines){
            println(StringUtil.concat(left, StringUtil.padRight(l, max_length - l.length()), right));
        }
        separatorLine('-', max_length + padding * 2 + 2);
        return this;
    }

    /**
     * Prints a "GOODBYE" banner, typically used when an application is shutting down.
     *
     * @return this instance for method chaining
     */
    public synchronized Console goodbye() {
        emptyLine();
        separatorLine();
        println(StringUtil.center("GOODBYE", LINE_WIDTH));
        separatorLine();
        emptyLine();
        return this;
    }

    /**
     * Clears the terminal screen by emitting {@link #CLEAR_SCREEN_ESCAPE_SEQUENCE}.
     *
     * @return this instance for method chaining
     */
    public synchronized Console clearScreen(){
        return print(CLEAR_SCREEN_ESCAPE_SEQUENCE);
    }

    /**
     * Erases the current terminal line by emitting {@link #ERASE_LINE_ESCAPE_SEQUENCE}.
     *
     * @return this instance for method chaining
     */
    public synchronized Console eraseLine(){
        return print(ERASE_LINE_ESCAPE_SEQUENCE);
    }

    /**
     * Displays a "PRESS CTRL-C TO EXIT" prompt and registers a JVM shutdown hook that marks this console as
     * exiting and prints a goodbye banner. Use together with {@link #waitForExit()} to keep an example program
     * running until the user terminates it.
     *
     * @return this instance for method chaining
     */
    public synchronized Console promptForExit(){
        box(4, "PRESS CTRL-C TO EXIT");
        emptyLine();
        exiting = false;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                exiting = true;
                goodbye();
            }
        });
        return this;
    }

    /**
     * Blocks the calling thread until this console has been flagged as exiting, polling periodically. Typically
     * called after {@link #promptForExit()} to keep the program alive until shutdown is requested.
     *
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void waitForExit() throws InterruptedException {
        while(!exiting){
            Thread.sleep(50);
        }
    }

    /**
     * Returns whether this console has been flagged as exiting (for example, after the shutdown hook fired).
     *
     * @return {@code true} if exit has been requested, otherwise {@code false}
     */
    public synchronized boolean exiting(){
        return exiting;
    }

    /**
     * Returns whether this console is still running, i.e. has not yet been flagged as exiting.
     *
     * @return {@code true} while exit has not been requested, otherwise {@code false}
     */
    public synchronized boolean isRunning(){
        return !exiting;
    }
}
