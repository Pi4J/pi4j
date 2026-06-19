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

public class StringUtil {

    /**
     * Constant <code>EMPTY=""</code>
     */
    public static final String EMPTY = "";
    /**
     * Constant <code>DEFAULT_PAD_CHAR=' '</code>
     */
    public static final char DEFAULT_PAD_CHAR = ' ';

    public static boolean isNullOrEmpty(String data, boolean trim) {
        if (data == null)
            return true;

        // trim if requested
        String test = data;
        if (trim)
            test = data.trim();

        return (test.length() <= 0);
    }

    public static boolean isNullOrEmpty(String data) {
        return isNullOrEmpty(data, false);
    }

    public static boolean isNotNullOrEmpty(String data) {
        return isNotNullOrEmpty(data, false);
    }

    public static boolean isNotNullOrEmpty(String data, boolean trim) {
        return !(isNullOrEmpty(data, trim));
    }

    public static String setIfNullOrEmpty(String data, String replacement, boolean trim) {
        if (isNullOrEmpty(data, trim)) {
            return replacement;
        }
        return data;
    }

    public static String setIfNullOrEmpty(String data, String replacement) {
        return setIfNullOrEmpty(data, replacement, false);
    }

    public static boolean contains(String source, String target) {
        return (null != source && null != target && source.contains(target));
    }

    /**
     * @param targets an array of {@link java.lang.String} objects.
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
     * @param sources an array of {@link java.lang.String} objects.
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
     * @param sources an array of {@link java.lang.String} objects.
     * @param targets an array of {@link java.lang.String} objects.
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

    public static String create(int length) {
        return create(DEFAULT_PAD_CHAR, length);
    }

    public static String create(char c, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (var index = 0; index < length; index++)
            sb.append(c);
        return sb.toString();
    }

    public static String create(String s, int length) {
        StringBuilder sb = new StringBuilder(length * s.length());
        for (var index = 0; index < length; index++)
            sb.append(s);
        return sb.toString();
    }

    public static String repeat(char c, int length) {
        return create(c, length);
    }

    public static String repeat(String s, int length) {
        return create(s, length);
    }

    public static String padLeft(String data, int length) {
        return padLeft(data, DEFAULT_PAD_CHAR, length);
    }

    public static String padLeft(String data, char pad, int length) {
        var sb = new StringBuilder(data.length() + length);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    public static String padLeft(String data, String pad, int length) {
        var sb = new StringBuilder(data.length() + (length * pad.length()));
        for (var index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    public static String padRight(String data, int length) {
        return padRight(data, DEFAULT_PAD_CHAR, length);
    }

    public static String padRight(String data, char pad, int length) {
        var sb = new StringBuilder(data.length() + length);
        sb.append(data);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    public static String padRight(String data, String pad, int length) {
        var sb = new StringBuilder(data.length() + (length * pad.length()));
        sb.append(data);
        for (var index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    public static String pad(String data, int length) {
        return pad(data, DEFAULT_PAD_CHAR, length);
    }

    public static String pad(String data, char pad, int length) {
        return create(pad, length) + data + create(pad, length);
    }

    public static String pad(String data, String pad, int length) {
        return create(pad, length) + data + create(pad, length);
    }

    public static String padCenter(String data, int length) {
        return padCenter(data, DEFAULT_PAD_CHAR, length);
    }

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

    public static String trimLeft(String data) {
        return trimLeft(data, DEFAULT_PAD_CHAR);
    }

    public static String trimLeft(String data, char trim) {
        for (var index = 0; index < data.length(); index++)
            if (!(data.charAt(index) == trim))
                return data.substring(index);
        return EMPTY;
    }

    public static String trimRight(String data) {
        return trimRight(data, DEFAULT_PAD_CHAR);
    }

    public static String trimRight(String data, char trim) {
        int count = 0;
        for (var index = data.length(); index > 0; index--)
            if (data.charAt(index - 1) == trim)
                count++;
            else
                return data.substring(0, data.length() - count);
        return EMPTY;
    }

    public static String trim(String data) {
        return trim(data, DEFAULT_PAD_CHAR);
    }

    public static String trim(String data, char trim) {
        var result = trimLeft(data, trim);
        return trimRight(result, trim);
    }

    public static String center(String text, int length) {
        var out = String.format("%" + length + "s%s%" + length + "s", "", text, "");
        var mid = (out.length() / 2);
        var start = mid - (length / 2);
        var end = start + length;
        return out.substring((int) start, (int) end);
    }

    public static String concat(String... data) {
        var sb = new StringBuilder();
        for (var d : data) {
            sb.append(d);
        }
        return sb.toString();
    }

    public static void appendHexString(StringBuilder builder, byte byt) {
        builder.append(String.format("%02X", byt));
    }

    public static String toHexString(byte byt) {
        return String.format("%02X", byt);
    }

    public static void appendHexString(StringBuilder builder, int byt) {
        builder.append(String.format("%02X", (byte) byt));
    }

    public static String toHexString(int byt) {
        return String.format("%02X", (byte) byt);
    }

    /**
     * @param bytes   an array of byte values.
     */
    public static void appendHexString(StringBuilder builder, byte[] bytes) {
        for (byte b : bytes) {
            builder.append(String.format("%02X ", b));
        }
    }

    public static String toHexString(CharSequence data) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, data);
        return sb.toString().trim();
    }

    public static void appendHexString(StringBuilder builder, CharSequence data) {
        appendHexString(builder, data.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * @param charset character set used to decode string to bytes
     */
    public static String toHexString(CharSequence data, Charset charset) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, data, charset);
        return sb.toString().trim();
    }

    /**
     * @param charset character set used to decode string to bytes
     */
    public static void appendHexString(StringBuilder builder, CharSequence data, Charset charset) {
        appendHexString(builder, data.toString().getBytes(charset));
    }

    /**
     * @param bytes an array of byte values.
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, bytes);
        return sb.toString().trim();
    }

    public static void appendHexString(StringBuilder builder, ByteBuffer buffer) {
        appendHexString(builder, buffer.array());
    }

    public static String toHexString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, buffer);
        return sb.toString().trim();
    }

    /**
     * @param bytes   an array of byte values.
     */
    public static void appendHexString(StringBuilder builder, byte[] bytes, int offset, int length) {
        appendHexString(builder, Arrays.copyOfRange(bytes, offset, offset + length));
    }

    /**
     * @param bytes  an array of byte values.
     */
    public static String toHexString(byte[] bytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, bytes, offset, length);
        return sb.toString().trim();
    }

    public static void appendHexString(StringBuilder builder, ByteBuffer buffer, int offset, int length) {
        appendHexString(builder, buffer.array(), offset, length);
    }

    public static String toHexString(ByteBuffer buffer, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        appendHexString(sb, buffer, offset, length);
        return sb.toString().trim();
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseInteger(String str, Integer defaultValue) {
        try {
            Integer v = Integer.parseInt(str);
            return v.intValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long parseLong(String str, Long defaultValue) {
        try {
            Long v = Long.parseLong(str);
            return v.intValue();
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

