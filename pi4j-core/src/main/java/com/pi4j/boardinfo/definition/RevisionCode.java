package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardInfoHelper.java
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

import java.util.Objects;

/**
 * Represents a parsed Raspberry Pi 32-bit revision code in the format:
 * <pre>NOQuuuWuFMMMCCCCPPPPTTTTTTTTRRRR</pre>
 * where:
 * <ul>
 *   <li>N - Overvoltage allowed</li>
 *   <li>O - OTP program allowed</li>
 *   <li>Q - OTP read allowed</li>
 *   <li>u - Unused</li>
 *   <li>W - Warranty voided</li>
 *   <li>F - New-style revision code flag</li>
 *   <li>MMM - Memory size</li>
 *   <li>CCCC - Manufacturer</li>
 *   <li>PPPP - Processor</li>
 *   <li>TTTTTTTT - Type</li>
 *   <li>RRRR - Revision</li>
 * </ul>
 *
 * <p>Both old-style and new-style revision codes are supported. New-style codes are identified
 * by bit 23 being set. Old-style codes are matched against known revision code tables.</p>
 *
 * <p>Instances are obtained via the factory methods {@link #of(int)} and {@link #of(String)}:</p>
 * <pre>
 *     RevisionCode code = RevisionCode.of("a03111");
 *     RevisionCode code = RevisionCode.of(0xa03111);
 * </pre>
 *
 * @see <a href="https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#new-style-revision-codes">
 *     Raspberry Pi New-Style Revision Codes</a>
 */
public final class RevisionCode {

    private final MemorySize memorySize;
    private final Processor processor;
    private final Type type;
    private final int revision;

    /**
     * Private constructor. Use {@link #of(int)} or {@link #of(String)} to obtain instances.
     *
     * @param memorySize the memory size decoded from the revision code
     * @param processor  the processor decoded from the revision code
     * @param type       the board type decoded from the revision code
     * @param revision   the PCB revision number decoded from the revision code
     */
    private RevisionCode(MemorySize memorySize, Processor processor, Type type, int revision) {
        this.memorySize = memorySize;
        this.processor = processor;
        this.type = type;
        this.revision = revision;
    }

    /**
     * Returns the memory size of the board.
     *
     * @return the {@link MemorySize} decoded from the revision code
     */
    public MemorySize memorySize() {
        return memorySize;
    }

    /**
     * Returns the processor of the board.
     *
     * @return the {@link Processor} decoded from the revision code
     */
    public Processor processor() {
        return processor;
    }

    /**
     * Returns the board type.
     *
     * @return the {@link Type} decoded from the revision code
     */
    public Type type() {
        return type;
    }

    /**
     * Returns the PCB revision number.
     * For old-style revision codes, this always returns {@code 1} since old-style boards
     * do not have revision-specific {@code BoardModel} instances.
     *
     * @return the PCB revision number
     */
    public int revision() {
        return revision;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RevisionCode that)) return false;
        return memorySize == that.memorySize
            && processor == that.processor
            && type == that.type
            && revision == that.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            memorySize,
            processor,
            type,
            revision);
    }

    @Override
    public String toString() {
        return "RevisionCode{" +
            "memorySize=" + memorySize +
            ", processor=" + processor +
            ", type=" + type +
            ", revision=" + revision +
            '}';
    }

    /**
     * Creates a {@code RevisionCode} by parsing a 32-bit integer revision code.
     *
     * @param revisionCode the raw 32-bit revision code
     * @return a {@code RevisionCode} instance representing the parsed fields
     */
    public static RevisionCode of(int revisionCode) {
        return new RevisionCode(
            MemorySize.fromRevisionCode(revisionCode),
            Processor.fromRevisionCode(revisionCode),
            Type.fromRevisionCode(revisionCode),
            extractRevision(revisionCode)
        );
    }

    /**
     * Creates a {@code RevisionCode} by parsing a hexadecimal string revision code.
     * The string may optionally include a {@code 0x} or {@code 0X} prefix and surrounding
     * whitespace, both of which are stripped before parsing.
     *
     * @param revisionCode the revision code as a hex string (e.g. {@code "a03111"} or {@code "0xa03111"})
     * @return a {@code RevisionCode} instance representing the parsed fields
     * @throws IllegalArgumentException if the string is {@code null}, blank, or not valid hexadecimal
     */
    public static RevisionCode of(String revisionCode) {
        if (revisionCode == null || revisionCode.isBlank()) {
            throw new IllegalArgumentException(("Revision code cannot be null or empty."));
        }
        final String clean = revisionCode.trim().toUpperCase().replace("0X", "").replace(" ", "");
        try {
            int value = (int) Long.parseLong(clean, 16);
            return of(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Revision code is not valid hex: " + revisionCode, e);
        }
    }

    /**
     * Returns {@code true} if the given revision code uses the new-style format,
     * identified by bit 23 being set.
     *
     * @param revisionCode the raw 32-bit revision code
     * @return {@code true} if new-style, {@code false} if old-style
     */
    private static boolean isNewStyleRevisionCode(int revisionCode) {
        return (revisionCode & 0x800000) != 0;
    }

    /**
     * Extracts the PCB revision number (bits 3:0) from a new-style revision code.
     * For old-style revision codes, returns {@code 1} since old-style boards do not
     * have revision-specific {@code BoardModel} instances.
     *
     * @param revisionCode the raw 32-bit revision code
     * @return the PCB revision number, or {@code 1} for old-style codes
     */
    private static int extractRevision(int revisionCode) {
        return isNewStyleRevisionCode(revisionCode) ? revisionCode & 0xF : 1;
    }

    /**
     * Represents the memory size field (bits 22:20) of a new-style Raspberry Pi revision code.
     *
     * <p>Old-style revision codes are matched against known values. New-style codes are
     * identified by the 3-bit MMM field. If the value is unrecognized, {@link #UNKNOWN} is returned.</p>
     *
     * @see <a href="https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#new-style-revision-codes">
     *     Raspberry Pi New-Style Revision Codes</a>
     */
    public enum MemorySize {
        MB_256(0, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007, 0x0008, 0x0009, 0x0012),
        MB_512(1, 0x000d, 0x000e, 0x000f, 0x0010, 0x0011, 0x0013, 0x0014),
        GB_1(2),
        GB_2(3),
        GB_4(4),
        GB_8(5),
        GB_16(6),
        OTHER(7, 0x0015),
        UNKNOWN(8);

        /** A list of old-style revision codes for the enum instance. */
        private final int[] oldStyleRevisionCodes;

        /** The value of the enum instance. */
        private final int value;

        MemorySize(int value) {
            this(value, new int[0]);
        }

        MemorySize(int value, int ... oldStyleRevisionCodes) {
            this.oldStyleRevisionCodes = oldStyleRevisionCodes;
            this.value = value;
        }

        /**
         * Returns the {@code MemorySize} corresponding to the given revision code.
         * Returns {@link #UNKNOWN} if no match is found.
         *
         * @param revisionCode the raw 32-bit revision code
         * @return the matching {@code MemorySize}, or {@link #UNKNOWN}
         */
        static MemorySize fromRevisionCode(int revisionCode) {
            if (isNewStyleRevisionCode(revisionCode)) {
                final int value = (revisionCode >> 20) & 0x7;
                for (var s : values()) {
                    if (s.value == value) {
                        return s;
                    }
                }
            } else {
                for (var s : values()) {
                    for (var r : s.oldStyleRevisionCodes) {
                        if (r == revisionCode) {
                            return s;
                        }
                    }
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Represents the processor field (bits 15:12) of a new-style Raspberry Pi revision code.
     *
     * <p>All boards using old-style revision codes are assumed to use the {@link #BCM2835}.
     * If the processor value in a new-style code is unrecognized, {@link #UNKNOWN} is returned.</p>
     *
     * @see <a href="https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#new-style-revision-codes">
     *     Raspberry Pi New-Style Revision Codes</a>
     */
    public enum Processor {
        BCM2835(0, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007, 0x0008, 0x0009, 0x000d, 0x000e, 0x000f, 0x0010, 0x0011, 0x0012, 0x0013, 0x0014, 0x0015),
        BCM2836(1),
        BCM2837(2),
        BCM2711(3),
        BCM2712(4),
        UNKNOWN(16);

        /** A list of old-style revision codes for the enum instance. */
        private final int[] oldStyleRevisionCodes;

        /** The value of the enum instance. */
        private final int value;

        Processor(int value) {
            this(value, new int[0]);
        }

        Processor(int value, int ... oldStyleRevisionCodes) {
            this.oldStyleRevisionCodes = oldStyleRevisionCodes;
            this.value = value;
        }

        /**
         * Returns the {@code Processor} corresponding to the given revision code.
         * Old-style revision codes always return {@link #BCM2835}.
         * Returns {@link #UNKNOWN} if no match is found in a new-style code.
         *
         * @param revisionCode the raw 32-bit revision code
         * @return the matching {@code Processor}, or {@link #UNKNOWN}
         */
        static Processor fromRevisionCode(int revisionCode) {
            if (isNewStyleRevisionCode(revisionCode)) {
                final int value = (revisionCode >> 12) & 0xF;
                for (var s : values()) {
                    if (s.value == value) {
                        return s;
                    }
                }
            } else {
                for (var s : values()) {
                    for (var r : s.oldStyleRevisionCodes) {
                        if (r == revisionCode) {
                            return s;
                        }
                    }
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Represents the board type field (bits 11:4) of a new-style Raspberry Pi revision code.
     *
     * <p>Old-style revision codes are matched against known values per type. New-style codes
     * are identified by the 8-bit TTTTTTTT field. If the value is unrecognized,
     * {@link #UNKNOWN} is returned.</p>
     *
     * @see <a href="https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#new-style-revision-codes">
     *     Raspberry Pi New-Style Revision Codes</a>
     */
    public enum Type {
        RPI_A(0, 0x0007, 0x0008, 0x0009),
        RPI_B(1, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x000d, 0x000e, 0x000f),
        RPI_A_PLUS(2, 0x0012, 0x0015),
        RPI_B_PLUS(3, 0x0010, 0x0013),
        RPI_2B(4),
        RPI_ALPHA(5),
        RPI_CM1(6, 0x0011, 0x0014),
        RPI_3B(8),
        RPI_ZERO(9),
        RPI_CM3(10),
        RPI_ZERO_W(12),
        RPI_3B_PLUS(13),
        RPI_3A_PLUS(14),
        RPI_CM3_PLUS(16),
        RPI_4B(17),
        RPI_ZERO_2_W(18),
        RPI_400(19),
        RPI_CM4(20),
        RPI_CM4S(21),
        RPI_5(23),
        RPI_CM5(24),
        RPI_500_500_PLUS(25),
        RPI_CM5_LITE(26),
        RPI_CM0(27),
        UNKNOWN(256);

        /** A list of old-style revision codes for the enum instance. */
        private final int[] oldStyleRevisionCodes;

        /** The value of the enum instance. */
        private final int value;

        Type(int value) {
            this(value, new int[0]);
        }

        Type(int value, int ... oldStyleRevisionCodes) {
            this.oldStyleRevisionCodes = oldStyleRevisionCodes;
            this.value = value;
        }

        /**
         * Returns the {@code Type} corresponding to the given revision code.
         * Returns {@link #UNKNOWN} if no match is found.
         *
         * @param revisionCode the raw 32-bit revision code
         * @return the matching {@code Type}, or {@link #UNKNOWN}
         */
        static Type fromRevisionCode(int revisionCode) {
            if (isNewStyleRevisionCode(revisionCode)) {
                final int value = (revisionCode >> 4) & 0xFF;
                for (var s : values()) {
                    if (s.value == value) {
                        return s;
                    }
                }
            } else {
                for (var s : values()) {
                    for (var r : s.oldStyleRevisionCodes) {
                        if (r == revisionCode) {
                            return s;
                        }
                    }
                }
            }
            return UNKNOWN;
        }
    }
}
