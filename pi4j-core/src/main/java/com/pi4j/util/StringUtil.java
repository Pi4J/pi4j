package com.pi4j.util;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  StringUtil.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * General-purpose string helpers used throughout Pi4J for null/empty checks, padding and centering, repetition,
 * trimming, hexadecimal rendering of bytes and buffers, and lenient numeric parsing. Layout helpers here back the
 * formatted output produced by {@link Console}.
 */
public class StringUtil {

    /** The empty string. */
    public static final String EMPTY = "";
    /** The default padding character (a space) used by the padding and centering helpers. */
    public static final char DEFAULT_PAD_CHAR = ' ';

    /**
     * Returns whether the given string is {@code null} or has no characters, optionally trimming first.
     *
     * @param data the string to test
     * @param trim if {@code true}, leading and trailing whitespace is removed before the length check
     * @return {@code true} if the (optionally trimmed) string is {@code null} or empty
     */
    public static boolean isNullOrEmpty(String data, boolean trim) {
        if (data == null)
            return true;

        // trim if requested
        String test = data;
        if (trim)
            test = data.trim();

        return (test.length() <= 0);
    }

    /**
     * Returns whether the given string is {@code null} or empty, without trimming.
     *
     * @param data the string to test
     * @return {@code true} if the string is {@code null} or has no characters
     */
    public static boolean isNullOrEmpty(String data) {
        return isNullOrEmpty(data, false);
    }

    /**
     * Returns whether the given string is non-{@code null} and non-empty, without trimming.
     *
     * @param data the string to test
     * @return {@code true} if the string contains at least one character
     */
    public static boolean isNotNullOrEmpty(String data) {
        return isNotNullOrEmpty(data, false);
    }

    /**
     * Returns whether the given string is non-{@code null} and non-empty, optionally trimming first.
     *
     * @param data the string to test
     * @param trim if {@code true}, leading and trailing whitespace is removed before the length check
     * @return {@code true} if the (optionally trimmed) string contains at least one character
     */
    public static boolean isNotNullOrEmpty(String data, boolean trim) {
        return !(isNullOrEmpty(data, trim));
    }

    /**
     * Returns the given string, or a replacement when it is {@code null} or empty.
     *
     * @param data        the candidate string
     * @param replacement the value to return when {@code data} is {@code null} or empty
     * @param trim        if {@code true}, {@code data} is trimmed before the emptiness check
     * @return {@code data} if it is non-empty, otherwise {@code replacement}
     */
    public static String setIfNullOrEmpty(String data, String replacement, boolean trim) {
        if (isNullOrEmpty(data, trim)) {
            return replacement;
        }
        return data;
    }

    /**
     * Returns the given string, or a replacement when it is {@code null} or empty (without trimming).
     *
     * @param data        the candidate string
     * @param replacement the value to return when {@code data} is {@code null} or empty
     * @return {@code data} if it is non-empty, otherwise {@code replacement}
     */
    public static String setIfNullOrEmpty(String data, String replacement) {
        return setIfNullOrEmpty(data, replacement, false);
    }

    /**
     * Returns whether the source string contains the target substring.
     *
     * @param source the string to search within
     * @param target the substring to look for
     * @return {@code true} if both arguments are non-{@code null} and {@code source} contains {@code target}
     */
    public static boolean contains(String source, String target) {
        return (null != source && null != target && source.contains(target));
    }

    /**
     * Returns whether the source string contains any of the target substrings.
     *
     * @param source  the string to search within
     * @param targets the candidate substrings to look for
     * @return {@code true} if {@code source} contains at least one of {@code targets}
     */
    public static boolean contains(String source, String[] targets) {
        if (null != source && null != targets) {
            for (var target : targets) {
                if (source.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether any of the source strings contains the target substring.
     *
     * @param sources the strings to search within
     * @param target  the substring to look for
     * @return {@code true} if at least one of {@code sources} contains {@code target}
     */
    public static boolean contains(String[] sources, String target) {
        if (null != sources && null != target) {
            for (var source : sources) {
                if (contains(source, target))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns whether any of the source strings contains any of the target substrings.
     *
     * @param sources the strings to search within
     * @param targets the candidate substrings to look for
     * @return {@code true} if at least one of {@code sources} contains at least one of {@code targets}
     */
    public static boolean contains(String[] sources, String[] targets) {
        if (null != sources && null != targets) {
            for (var source : sources) {
                if (contains(source, targets))
                    return true;
            }
        }
        return false;
    }

    /**
     * Creates a string of the given length filled with the default pad character.
     *
     * @param length the number of characters in the resulting string
     * @return a string of spaces of the requested length
     */
    public static String create(int length) {
        return create(DEFAULT_PAD_CHAR, length);
    }

    /**
     * Creates a string consisting of the given character repeated the requested number of times.
     *
     * @param c      the character to repeat
     * @param length the number of repetitions
     * @return the resulting string
     */
    public static String create(char c, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (var index = 0; index < length; index++)
            sb.append(c);
        return sb.toString();
    }

    /**
     * Creates a string consisting of the given substring repeated the requested number of times.
     *
     * @param s      the substring to repeat
     * @param length the number of repetitions
     * @return the resulting string
     */
    public static String create(String s, int length) {
        StringBuilder sb = new StringBuilder(length * s.length());
        for (var index = 0; index < length; index++)
            sb.append(s);
        return sb.toString();
    }

    /**
     * Returns the given character repeated the requested number of times.
     *
     * @param c      the character to repeat
     * @param length the number of repetitions
     * @return the resulting string
     */
    public static String repeat(char c, int length) {
        return create(c, length);
    }

    /**
     * Returns the given substring repeated the requested number of times.
     *
     * @param s      the substring to repeat
     * @param length the number of repetitions
     * @return the resulting string
     */
    public static String repeat(String s, int length) {
        return create(s, length);
    }

    /**
     * Pads the given string on the left with the default pad character.
     *
     * @param data   the string to pad
     * @param length the number of pad characters to prepend
     * @return the left-padded string
     */
    public static String padLeft(String data, int length) {
        return padLeft(data, DEFAULT_PAD_CHAR, length);
    }

    /**
     * Pads the given string on the left with the specified pad character.
     *
     * @param data   the string to pad
     * @param pad    the character to prepend
     * @param length the number of pad characters to prepend
     * @return the left-padded string
     */
    public static String padLeft(String data, char pad, int length) {
        var sb = new StringBuilder(data.length() + length);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    /**
     * Pads the given string on the left with the specified pad string repeated a number of times.
     *
     * @param data   the string to pad
     * @param pad    the substring to prepend
     * @param length the number of times {@code pad} is prepended
     * @return the left-padded string
     */
    public static String padLeft(String data, String pad, int length) {
        var sb = new StringBuilder(data.length() + (length * pad.length()));
        for (var index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    /**
     * Pads the given string on the right with the default pad character.
     *
     * @param data   the string to pad
     * @param length the number of pad characters to append
     * @return the right-padded string
     */
    public static String padRight(String data, int length) {
        return padRight(data, DEFAULT_PAD_CHAR, length);
    }

    /**
     * Pads the given string on the right with the specified pad character.
     *
     * @param data   the string to pad
     * @param pad    the character to append
     * @param length the number of pad characters to append
     * @return the right-padded string
     */
    public static String padRight(String data, char pad, int length) {
        var sb = new StringBuilder(data.length() + length);
        sb.append(data);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    /**
     * Pads the given string on the right with the specified pad string repeated a number of times.
     *
     * @param data   the string to pad
     * @param pad    the substring to append
     * @param length the number of times {@code pad} is appended
     * @return the right-padded string
     */
    public static String padRight(String data, String pad, int length) {
        var sb = new StringBuilder(data.length() + (length * pad.length()));
        sb.append(data);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    /**
     * Surrounds the given string with the default pad character on both sides.
     *
     * @param data   the string to pad
     * @param length the number of pad characters added on each side
     * @return the padded string
     */
    public static String pad(String data, int length) {
        return pad(data, DEFAULT_PAD_CHAR, length);
    }

    /**
     * Surrounds the given string with the specified pad character on both sides.
     *
     * @param data   the string to pad
     * @param pad    the character added on each side
     * @param length the number of pad characters added on each side
     * @return the padded string
     */
    public static String pad(String data, char pad, int length) {
        return create(pad, length) + data + create(pad, length);
    }

    /**
     * Surrounds the given string with the specified pad string on both sides.
     *
     * @param data   the string to pad
     * @param pad    the substring added on each side
     * @param length the number of times {@code pad} is added on each side
     * @return the padded string
     */
    public static String pad(String data, String pad, int length) {
        return create(pad, length) + data + create(pad, length);
    }

    /**
     * Centers the given string within a field of the requested width using the default pad character. If the
     * string is already at least as long as the width, it is returned unchanged.
     *
     * @param data   the string to center
     * @param length the total width of the resulting field
     * @return the centered string
     */
    public static String padCenter(String data, int length) {
        return padCenter(data, DEFAULT_PAD_CHAR, length);
    }

    /**
     * Centers the given string within a field of the requested width using the specified pad character. If the
     * string is already at least as long as the width, it is returned unchanged.
     *
     * @param data   the string to center
     * @param pad    the character used to fill the surrounding space
     * @param length the total width of the resulting field
     * @return the centered string
     */
    public static String padCenter(String data, char pad, int length) {
        if (data.length() < length) {
            int needed = length - data.length();
            int padNeeded = needed / 2;
            StringBuilder result = new StringBuilder();
            result.append(create(pad, padNeeded));
            result.append(data);
            result.append(create(pad, padNeeded));
            int remaining = length - result.length();
            result.append(create(pad, remaining));
            return result.toString();
        }
        return data;
    }

    /**
     * Removes leading occurrences of the default pad character from the given string.
     *
     * @param data the string to trim
     * @return the string with leading pad characters removed, or {@link #EMPTY} if it consisted only of them
     */
    public static String trimLeft(String data) {
        return trimLeft(data, DEFAULT_PAD_CHAR);
    }

    /**
     * Removes leading occurrences of the given character from the string.
     *
     * @param data the string to trim
     * @param trim the character to strip from the start
     * @return the string with leading {@code trim} characters removed, or {@link #EMPTY} if it consisted only of them
     */
    public static String trimLeft(String data, char trim) {
        for (var index = 0; index < data.length(); index++)
            if (!(data.charAt(index) == trim))
                return data.substring(index);
        return EMPTY;
    }

    /**
     * Removes trailing occurrences of the default pad character from the given string.
     *
     * @param data the string to trim
     * @return the string with trailing pad characters removed, or {@link #EMPTY} if it consisted only of them
     */
    public static String trimRight(String data) {
        return trimRight(data, DEFAULT_PAD_CHAR);
    }

    /**
     * Removes trailing occurrences of the given character from the string.
     *
     * @param data the string to trim
     * @param trim the character to strip from the end
     * @return the string with trailing {@code trim} characters removed, or {@link #EMPTY} if it consisted only of them
     */
    public static String trimRight(String data, char trim) {
        int count = 0;
        for (var index = data.length(); index > 0; index--)
            if (data.charAt(index - 1) == trim)
                count++;
            else
                return data.substring(0, data.length() - count);
        return EMPTY;
    }

    /**
     * Removes leading and trailing occurrences of the default pad character from the given string.
     *
     * @param data the string to trim
     * @return the string with leading and trailing pad characters removed
     */
    public static String trim(String data) {
        return trim(data, DEFAULT_PAD_CHAR);
    }

    /**
     * Removes leading and trailing occurrences of the given character from the string.
     *
     * @param data the string to trim
     * @param trim the character to strip from both ends
     * @return the string with leading and trailing {@code trim} characters removed
     */
    public static String trim(String data, char trim) {
        var result = trimLeft(data, trim);
        return trimRight(result, trim);
    }

    /**
     * Returns the given text centered within a field of the requested width. The text is surrounded by spaces so
     * that it sits in the middle of a fixed-width column.
     *
     * @param text   the text to center
     * @param length the total width of the resulting field
     * @return the centered text
     */
    public static String center(String text, int length) {
        var out = String.format("%" + length + "s%s%" + length + "s", "", text, "");
        var mid = (out.length() / 2);
        var start = mid - (length / 2);
        var end = start + length;
        return out.substring((int) start, (int) end);
    }

    /**
     * Concatenates the given strings in order.
     *
     * @param data the strings to join
     * @return the concatenation of all arguments
     */
    public static String concat(String... data) {
        var sb = new StringBuilder();
        for (var d : data) {
            sb.append(d);
        }
        return sb.toString();
    }

    /**
     * Appends the two-digit uppercase hexadecimal representation of a byte to the given builder.
     *
     * @param builder the builder to append to
     * @param byt     the byte to render
     */
    public static void appendHexString(StringBuilder builder, byte byt) {
        builder.append(String.format("%02X", byt));
    }

    /**
     * Returns the two-digit uppercase hexadecimal representation of a byte.
     *
     * @param byt the byte to render
     * @return the two-character hex string
     */
    public static String toHexString(byte byt) {
        return String.format("%02X", byt);
    }

    /**
     * Appends the two-digit uppercase hexadecimal representation of the low byte of an int to the given builder.
     *
     * @param builder the builder to append to
     * @param byt     the value whose least-significant byte is rendered
     */
    public static void appendHexString(StringBuilder builder, int byt) {
        builder.append(String.format("%02X", (byte) byt));
    }

    /**
     * Returns the two-digit uppercase hexadecimal representation of the low byte of an int.
     *
     * @param byt the value whose least-significant byte is rendered
     * @return the two-character hex string
     */
    public static String toHexString(int byt) {
        return String.format("%02X", (byte) byt);
    }

    /**
     * Appends the space-separated, two-digit uppercase hexadecimal representation of each byte to the builder.
     *
     * @param builder the builder to append to
     * @param bytes   the bytes to render
     */
    public static void appendHexString(StringBuilder builder, byte[] bytes) {
        for (byte b : bytes) {
            builder.append(String.format("%02X ", b));
        }
    }

    /**
     * Returns the space-separated, two-digit uppercase hexadecimal representation of the given text, decoded as
     * US-ASCII bytes.
     *
     * @param data the text to render
     * @return the trimmed hex string
     */
    public static String toHexString(CharSequence data) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, data);
        return sb.toString().trim();
    }

    /**
     * Appends the space-separated, two-digit uppercase hexadecimal representation of the given text to the
     * builder, decoded as US-ASCII bytes.
     *
     * @param builder the builder to append to
     * @param data    the text to render
     */
    public static void appendHexString(StringBuilder builder, CharSequence data) {
        appendHexString(builder, data.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * Returns the space-separated, two-digit uppercase hexadecimal representation of the given text, decoded with
     * the specified charset.
     *
     * @param data    the text to render
     * @param charset character set used to encode the text to bytes
     * @return the trimmed hex string
     */
    public static String toHexString(CharSequence data, Charset charset) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, data, charset);
        return sb.toString().trim();
    }

    /**
     * Appends the space-separated, two-digit uppercase hexadecimal representation of the given text to the
     * builder, decoded with the specified charset.
     *
     * @param builder the builder to append to
     * @param data    the text to render
     * @param charset character set used to encode the text to bytes
     */
    public static void appendHexString(StringBuilder builder, CharSequence data, Charset charset) {
        appendHexString(builder, data.toString().getBytes(charset));
    }

    /**
     * Returns the space-separated, two-digit uppercase hexadecimal representation of the given bytes.
     *
     * @param bytes the bytes to render
     * @return the trimmed hex string
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, bytes);
        return sb.toString().trim();
    }

    /**
     * Appends the space-separated, two-digit uppercase hexadecimal representation of the buffer's backing array
     * to the builder.
     *
     * @param builder the builder to append to
     * @param buffer  the buffer whose backing array is rendered
     */
    public static void appendHexString(StringBuilder builder, ByteBuffer buffer) {
        appendHexString(builder, buffer.array());
    }

    /**
     * Returns the space-separated, two-digit uppercase hexadecimal representation of the buffer's backing array.
     *
     * @param buffer the buffer whose backing array is rendered
     * @return the trimmed hex string
     */
    public static String toHexString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, buffer);
        return sb.toString().trim();
    }

    /**
     * Appends the hexadecimal representation of a sub-range of the given bytes to the builder.
     *
     * @param builder the builder to append to
     * @param bytes   the source byte array
     * @param offset  the index of the first byte to render
     * @param length  the number of bytes to render
     */
    public static void appendHexString(StringBuilder builder, byte[] bytes, int offset, int length) {
        appendHexString(builder, Arrays.copyOfRange(bytes, offset, offset + length));
    }

    /**
     * Returns the hexadecimal representation of a sub-range of the given bytes.
     *
     * @param bytes  the source byte array
     * @param offset the index of the first byte to render
     * @param length the number of bytes to render
     * @return the trimmed hex string
     */
    public static String toHexString(byte[] bytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, bytes, offset, length);
        return sb.toString().trim();
    }

    /**
     * Appends the hexadecimal representation of a sub-range of the buffer's backing array to the builder.
     *
     * @param builder the builder to append to
     * @param buffer  the buffer whose backing array is rendered
     * @param offset  the index of the first byte to render
     * @param length  the number of bytes to render
     */
    public static void appendHexString(StringBuilder builder, ByteBuffer buffer, int offset, int length) {
        appendHexString(builder, buffer.array(), offset, length);
    }

    /**
     * Returns the hexadecimal representation of a sub-range of the buffer's backing array.
     *
     * @param buffer the buffer whose backing array is rendered
     * @param offset the index of the first byte to render
     * @param length the number of bytes to render
     * @return the trimmed hex string
     */
    public static String toHexString(ByteBuffer buffer, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, buffer, offset, length);
        return sb.toString().trim();
    }

    /**
     * Returns whether the given string can be parsed as a floating-point number.
     *
     * @param str the string to test
     * @return {@code true} if the string parses as a number, otherwise {@code false}
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses the given string as an int, returning a default when it is not a valid integer.
     *
     * @param str          the string to parse
     * @param defaultValue the value returned when {@code str} cannot be parsed
     * @return the parsed integer, or {@code defaultValue} on a parse failure
     */
    public static int parseInteger(String str, Integer defaultValue) {
        try {
            Integer v = Integer.parseInt(str);
            return v.intValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parses the given string as a long, returning a default when it is not a valid long.
     *
     * @param str          the string to parse
     * @param defaultValue the value returned when {@code str} cannot be parsed
     * @return the parsed value, or {@code defaultValue} on a parse failure
     */
    public static long parseLong(String str, Long defaultValue) {
        try {
            Long v = Long.parseLong(str);
            return v.intValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

