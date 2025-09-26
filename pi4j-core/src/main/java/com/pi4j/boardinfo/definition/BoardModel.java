package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardModel.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.boardinfo.definition.BoardType.*;

/**
 * Represents various Raspberry Pi board models along with their specifications and attributes.
 *
 * <p>This class is partially based on resources such as Raspberry Pi documentation, GitHub, and other online references.</p>
 *
 * @see <a href="https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#new-style-revision-codes-in-use">Board Codes</a>
 * @see <a href="https://community.volumio.com/t/guide-identifying-your-raspberry-pi-board-on-volumio-a-comprehensive-guide-to-revision-codes/71350">Identifying Your Raspberry Pi Board</a>
 * @see <a href="https://github.com/raspberrypi/documentation/blob/develop/documentation/asciidoc/computers/raspberry-pi/revision-codes.adoc">Old-style Revision Codes</a>
 * @see <a href="https://en.wikipedia.org/wiki/Raspberry_Pi#Specifications">Raspberry Pi Specifications</a>
 * @see <a href="https://oastic.com/posts/how-to-know-which-raspberry-do-you-have/">How to Identify Your Raspberry Pi</a>
 * @see <a href="https://www.raspberrypi-spy.co.uk/2012/09/checking-your-raspberry-pi-board-version/">Checking Your Raspberry Pi Board Version</a>
 */
public enum BoardModel {
    MODEL_1_A("Raspberry Pi 1 Model A",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("0007", "0008", "0009"),
        PiModel.MODEL_A,
        HeaderVersion.TYPE_1,
        LocalDate.of(2013, 2, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(700),
        List.of(256 * 1024)),
    MODEL_1_A_PLUS("Raspberry Pi 1 Model A+",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("0012", "0015", "900021"),
        PiModel.MODEL_A,
        HeaderVersion.TYPE_1,
        LocalDate.of(2014, 11, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(700),
        List.of(256 * 1024, 512 * 1024),
        List.of("Amount of memory changed to 512Mb on 20160810"),
        false),
    MODEL_3_A_PLUS("Raspberry Pi 3 Model A+",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_3,
        List.of("9020e0", "9020e1"),
        PiModel.MODEL_A,
        HeaderVersion.TYPE_3,
        LocalDate.of(2018, 11, 1),
        Soc.BCM2837B0,
        Cpu.CORTEX_A53, 4,
        List.of(1400),
        List.of(512 * 1024),
        new ArrayList<>(),
        false),
    MODEL_1_B("Raspberry Pi 1 Model B",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("0002", "0003", "0004", "0005", "0006", "000d", "000e", "000f"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_1,
        LocalDate.of(2012, 4, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(700),
        List.of(256 * 1024, 512 * 1024),
        List.of("Amount of memory changed to 512Mb on 20121015"),
        false),
    MODEL_1_B_PLUS("Raspberry Pi 1 Model B+",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("0010", "0013", "900032"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_1,
        LocalDate.of(2014, 7, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(700),
        List.of(512 * 1024)),
    MODEL_2_B("Raspberry Pi 2 Model B",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_2,
        List.of("a01040", "a01041", "a21041"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_2,
        LocalDate.of(2015, 2, 1),
        Soc.BCM2836,
        Cpu.CORTEX_A7, 4,
        List.of(900),
        List.of(1024 * 1024)),
    MODEL_2_B_V1_2("Raspberry Pi 2 Model B V1.2",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_2,
        List.of("a02042", "a22042"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_2,
        LocalDate.of(2016, 10, 1),
        Soc.BCM2837,
        Cpu.CORTEX_A53, 4,
        List.of(900),
        List.of(1024 * 1024)),
    MODEL_3_B("Raspberry Pi 3 Model B",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_3,
        List.of("a02082", "a22082", "a32082", "a52082", "a22083"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2016, 2, 1),
        Soc.BCM2837,
        Cpu.CORTEX_A53, 4,
        List.of(1200),
        List.of(1024 * 1024)),
    MODEL_3_B_PLUS("Raspberry Pi 3 Model B+",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_3,
        List.of("a020d3", "a020d4"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2018, 3, 14),
        Soc.BCM2837B0,
        Cpu.CORTEX_A53, 4,
        List.of(1400),
        List.of(1024 * 1024)),
    MODEL_4_B("Raspberry Pi 4 Model B",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_4,
        List.of("a03111", "b03111", "b03112", "b03114", "b03115", "c03111", "c03112", "c03114", "c03115", "d03114", "d03115"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2019, 6, 24),
        Soc.BCM2711,
        Cpu.CORTEX_A72, 4,
        List.of(1500, 1800),
        List.of(1024 * 1024, 2048 * 1024, 4096 * 1024, 8192 * 1024)),
    MODEL_400("Raspberry Pi 400",
        ALL_IN_ONE_COMPUTER,
        Generation.GENERATION_4,
        List.of("c03130"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2020, 11, 2),
        Soc.BCM2711C0,
        Cpu.CORTEX_A72, 4,
        List.of(1800),
        List.of(4096 * 1024)),
    MODEL_5_B("Raspberry Pi 5 Model B",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_5,
        List.of("b04170", "c04170", "d04170", "e04171"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2023, 9, 28),
        Soc.BCM2712,
        Cpu.CORTEX_A76, 4,
        List.of(2400),
        List.of(2048 * 1024, 4096 * 1024, 8192 * 1024, 16384 * 1024),
        new ArrayList<>(),
        true),
    MODEL_500("Raspberry Pi 500",
        ALL_IN_ONE_COMPUTER,
        Generation.GENERATION_5,
        List.of("d04190"),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.of(2024, 12, 9),
        Soc.BCM2712,
        Cpu.CORTEX_A76, 4,
        List.of(2400),
        List.of(8192 * 1024),
        List.of(
            "20241209: Confirmed by Jeff Geerling who has evaluation version: d04190."
        ),
        true),
    COMPUTE_1("Compute Module 1",
        STACK_ON_COMPUTER,
        Generation.GENERATION_1,
        List.of("0011", "0014", "900061"),
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2014, 4, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(700),
        List.of(512 * 1024)),
    COMPUTE_3("Compute Module 3",
        STACK_ON_COMPUTER,
        Generation.GENERATION_3,
        List.of("a020a0", "a220a0"),
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2017, 1, 1),
        Soc.BCM2837,
        Cpu.CORTEX_A53, 4,
        List.of(1200),
        List.of(1024 * 1024)),
    COMPUTE_3_PLUS("Compute Module 3+",
        STACK_ON_COMPUTER,
        Generation.GENERATION_3,
        List.of("a02100"),
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2019, 1, 1),
        Soc.BCM2837B0,
        Cpu.CORTEX_A53, 4,
        List.of(1200),
        List.of(1024 * 1024)),
    COMPUTE_4("Compute Module 4",
        STACK_ON_COMPUTER,
        Generation.GENERATION_4,
        List.of("a03140", "b03140", "c03140", "d03140", "a03141", "b03141", "c03141", "d03141"),
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2020, 10, 1),
        Soc.BCM2711,
        Cpu.CORTEX_A72, 4,
        List.of(1500),
        List.of(1024 * 1024, 2048 * 1024, 4096 * 1024, 8192 * 1024)),
    // https://datasheets.raspberrypi.com/cm4s/cm4s-datasheet.pdf
    COMPUTE_4_SODIMM("Compute Module 4 SODIMM",
        STACK_ON_COMPUTER,
        Generation.GENERATION_4,
        new ArrayList<>(), // Not known yet
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2020, 10, 1),
        Soc.BCM2711,
        Cpu.CORTEX_A72, 4,
        List.of(1500),
        List.of(1024 * 1024, 2048 * 1024, 4096 * 1024, 8192 * 1024)),
    // https://www-cnx--software-com.cdn.ampproject.org/c/s/www.cnx-software.com/2024/11/27/raspberry-pi-cm5-broadcom-bcm2712-soc-16gb-lpddr4-ecc-memory/?amp=1
    COMPUTE_5("Compute Module 5",
        STACK_ON_COMPUTER,
        Generation.GENERATION_5,
        List.of("a04180", "b04180", "c04180", "d04180", "a041a0", "b041a0", "c041a0", "c041a0"),
        PiModel.COMPUTE,
        HeaderVersion.COMPUTE,
        LocalDate.of(2024, 11, 27),
        Soc.BCM2712,
        Cpu.CORTEX_A76, 4,
        List.of(2400),
        List.of(2048 * 1024, 4096 * 1024, 8192 * 1024, 16384 * 1024),
        List.of(
            "20241127: The board codes are not documented yet with the Compute 5 announcement.",
            "20241129: Confirmed by Jeff Geerling who has evaluation version: c04180 for the 4Gb version.",
            "When compared with Compute 4, we can assume the other boards should have be a, b, d.",
            "Will be further completed or modified when more info is available."
        ),
        true),
    ZERO_PCB_1_2("Raspberry Pi Zero PCB V1.2",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("900092", "920092"),
        PiModel.ZERO,
        HeaderVersion.TYPE_3,
        LocalDate.of(2015, 11, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(1000),
        List.of(512 * 1024)),
    ZERO_PCB_1_3("Raspberry Pi Zero PCB V1.3",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("900093", "920093"),
        PiModel.ZERO,
        HeaderVersion.TYPE_3,
        LocalDate.of(2016, 5, 1),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(1000),
        List.of(512 * 1024)),
    ZERO_W("Raspberry Pi Zero W",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_1,
        List.of("9000c1"),
        PiModel.ZERO,
        HeaderVersion.TYPE_3,
        LocalDate.of(2017, 2, 28),
        Soc.BCM2835,
        Cpu.ARM1176JZF_S, 1,
        List.of(1000),
        List.of(512 * 1024)),
    ZERO_V2("Raspberry Pi Zero V2",
        SINGLE_BOARD_COMPUTER,
        Generation.GENERATION_4,
        List.of("902120"),
        PiModel.ZERO,
        HeaderVersion.TYPE_3,
        LocalDate.of(2021, 10, 28),
        Soc.BCM2710A1,
        Cpu.CORTEX_A53, 4,
        List.of(1000),
        List.of(512 * 1024)),
    PICO("Raspberry Pi Pico",
        MICROCONTROLLER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.PICO,
        HeaderVersion.PICO,
        LocalDate.of(2021, 1, 1),
        Soc.RP2040,
        Cpu.CORTEX_MO_PLUS, 1,
        List.of(133),
        List.of(264 + 2048)),
    PICO_W("Raspberry Pi Pico W",
        MICROCONTROLLER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.PICO,
        HeaderVersion.PICO,
        LocalDate.of(2022, 6, 1),
        Soc.RP2040,
        Cpu.CORTEX_MO_PLUS, 1,
        List.of(133),
        List.of(264 + 2048),
        List.of("Same form factor as PICO but with Wi-Fi"),
        false),
    PICO_2("Raspberry Pi Pico 2",
        MICROCONTROLLER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.PICO,
        HeaderVersion.PICO,
        LocalDate.of(2024, 8, 8),
        Soc.RP2350,
        Cpu.CORTEX_M33, 1,
        List.of(150),
        List.of(520 + 4096)),
    PICO_2_W("Raspberry Pi Pico 2 W",
        MICROCONTROLLER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.PICO,
        HeaderVersion.PICO,
        LocalDate.of(2024, 8, 8),
        Soc.RP2350,
        Cpu.CORTEX_M33, 1,
        List.of(150),
        List.of(520 + 4096)),
    // Generic model, this can be used to force the library
    // to load Raspberry Pi plugins on other board types
    GENERIC("Generic board compatible with Raspberry Pi 4",
        SINGLE_BOARD_COMPUTER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.now(),
        Soc.UNKNOWN,
        Cpu.UNKNOWN, 4,
        new ArrayList<>(),
        new ArrayList<>()),
    GENERIC_RP1("Generic board compatible with Raspberry Pi 5",
        SINGLE_BOARD_COMPUTER,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.MODEL_B,
        HeaderVersion.TYPE_3,
        LocalDate.now(),
        Soc.UNKNOWN,
        Cpu.UNKNOWN, 4,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        true),
    // Unknown model
    UNKNOWN("Unknown",
        BoardType.UNKNOWN,
        Generation.UNKNOWN,
        new ArrayList<>(),
        PiModel.UNKNOWN,
        HeaderVersion.UNKNOWN,
        null,
        Soc.UNKNOWN,
        Cpu.UNKNOWN, 0,
        new ArrayList<>(),
        new ArrayList<>());

    private static final Logger logger = LoggerFactory.getLogger(BoardModel.class);

    private final String label;
    private final BoardType boardType;
    private final Generation generation;
    private final List<String> boardCodes;
    private final PiModel model;
    private final HeaderVersion headerVersion;
    private final LocalDate releaseDate;
    private final Soc soc;
    private final Cpu cpu;
    private final Integer numberOfCpu;
    private final List<Integer> versionsProcessorSpeedInMhz;
    private final List<Integer> versionsMemoryInKb;
    private final List<String> remarks;
    private final Boolean usesRP1;

    /**
     * Constructor for creating a {@code BoardModel} without remarks.
     *
     * @param label                       the descriptive name of the board
     * @param boardType                   the type of the board
     * @param boardCodes                  a list of unique codes identifying this board
     * @param model                       the Pi model of the board
     * @param headerVersion               the header version
     * @param releaseDate                 the release date of the board
     * @param soc                         the system-on-chip used
     * @param cpu                         the CPU type
     * @param numberOfCpu                 the number of CPU cores
     * @param versionsProcessorSpeedInMhz list of processor speeds in MHz
     * @param versionsMemoryInKb          list of memory sizes in KB
     */
    BoardModel(String label, BoardType boardType, Generation generation, List<String> boardCodes,
               PiModel model, HeaderVersion headerVersion, LocalDate releaseDate,
               Soc soc, Cpu cpu, Integer numberOfCpu,
               List<Integer> versionsProcessorSpeedInMhz, List<Integer> versionsMemoryInKb) {
        this(label, boardType, generation, boardCodes, model, headerVersion, releaseDate, soc, cpu, numberOfCpu,
            versionsProcessorSpeedInMhz, versionsMemoryInKb, new ArrayList<>(), false);
    }

    /**
     * Constructor for creating a {@code BoardModel}.
     *
     * @param label                       the descriptive name of the board
     * @param boardType                   the type of the board
     * @param generation                  the generation of the board (e.g. 1, 2, 3, 4)
     * @param boardCodes                  a list of unique codes identifying this board
     * @param model                       the Pi model of the board
     * @param headerVersion               the header version
     * @param releaseDate                 the release date of the board
     * @param soc                         the system-on-chip used
     * @param cpu                         the CPU type
     * @param numberOfCpu                 the number of CPU cores
     * @param versionsProcessorSpeedInMhz list of processor speeds in MHz
     * @param versionsMemoryInKb          list of memory sizes in KB
     * @param remarks                     any remarks or notes about the board
     * @param usesRP1                     board contains the RP1 chip to control GPIOs
     */
    BoardModel(String label, BoardType boardType, Generation generation, List<String> boardCodes,
               PiModel model, HeaderVersion headerVersion, LocalDate releaseDate,
               Soc soc, Cpu cpu, Integer numberOfCpu,
               List<Integer> versionsProcessorSpeedInMhz, List<Integer> versionsMemoryInKb,
               List<String> remarks,
               Boolean usesRP1) {
        this.label = label;
        this.boardType = boardType;
        this.generation = generation;
        this.boardCodes = boardCodes;
        this.model = model;
        this.headerVersion = headerVersion;
        this.releaseDate = releaseDate;
        this.soc = soc;
        this.cpu = cpu;
        this.numberOfCpu = numberOfCpu;
        this.versionsProcessorSpeedInMhz = versionsProcessorSpeedInMhz;
        this.versionsMemoryInKb = versionsMemoryInKb;
        this.remarks = remarks;
        this.usesRP1 = usesRP1;
    }

    /**
     * Retrieves the board model corresponding to the given board code.
     *
     * @param boardCode the board code to look up
     * @return the matching {@code BoardModel} or {@code UNKNOWN} if no match is found
     * @throws Exception if multiple matches are found
     */
    public static BoardModel getByBoardCode(String boardCode) throws Exception {
        var matches = Arrays.stream(BoardModel.values())
            .filter(bm -> bm.boardCodes.contains(boardCode))
            .collect(Collectors.toList());
        if (matches.isEmpty()) {
            return BoardModel.UNKNOWN;
        } else if (matches.size() > 1) {
            throw new Exception("Too many matching models found for code " + boardCode);
        }
        return matches.get(0);
    }

    /**
     * Retrieves the board model corresponding to the given board name.
     *
     * @param boardName the name of the board
     * @return the matching {@code BoardModel} or {@code UNKNOWN} if no match is found
     */
    public static BoardModel getByBoardName(String boardName) {
        var matches = Arrays.stream(BoardModel.values())
            .filter(bm -> boardName.toLowerCase().startsWith(bm.label.toLowerCase()))
            .collect(Collectors.toList());
        if (matches.isEmpty()) {
            return BoardModel.UNKNOWN;
        } else if (matches.size() > 1) {
            logger.error("Too many matching models found for name {}, the given name is not exclusive enough", boardName);
        }
        return matches.get(0);
    }

    /**
     * Retrieves all unique board codes from all models.
     *
     * @return a list of all board codes
     */
    public static List<String> getAllBoardCodes() {
        return Arrays.stream(BoardModel.values())
            .flatMap(b -> b.boardCodes.stream())
            .collect(Collectors.toList());
    }

    /**
     * @return the enum name of the board model
     */
    public String getName() {
        return name();
    }

    /**
     * @return the label of the board model
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the board type
     */
    public BoardType getBoardType() {
        return boardType;
    }

    /**
     * @return the generation
     */
    public Generation getGeneration() {
        return generation;
    }

    /**
     * @return the list of board codes
     */
    public List<String> getBoardCodes() {
        return boardCodes;
    }

    /**
     * @return the Pi model of the board
     */
    public PiModel getModel() {
        return model;
    }

    /**
     * @return the header version of the board
     */
    public HeaderVersion getHeaderVersion() {
        return headerVersion;
    }

    /**
     * @return the release date of the board
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * @return the system-on-chip used by the board
     */
    public Soc getSoc() {
        return soc;
    }

    /**
     * @return the CPU type used by the board
     */
    public Cpu getCpu() {
        return cpu;
    }

    /**
     * @return the number of CPU cores
     */
    public Integer getNumberOfCpu() {
        return numberOfCpu;
    }

    /**
     * @return a list of processor speeds in MHz
     */
    public List<Integer> getVersionsProcessorSpeedInMhz() {
        return versionsProcessorSpeedInMhz;
    }

    /**
     * @return a list of memory sizes in KB
     */
    public List<Integer> getVersionsMemoryInKb() {
        return versionsMemoryInKb;
    }

    /**
     * @return a list of memory sizes in MB
     */
    public List<Float> getVersionsMemoryInMb() {
        return versionsMemoryInKb.stream().map(m -> m / 1024F).collect(Collectors.toList());
    }

    /**
     * @return a list of memory sizes in GB
     */
    public List<Float> getVersionsMemoryInGb() {
        return versionsMemoryInKb.stream().map(m -> m / 1024F / 1024F).collect(Collectors.toList());
    }

    /**
     * @return any remarks associated with the board
     */
    public List<String> getRemarks() {
        return remarks;
    }

    /**
     * @return board contains the RP1 chip to control GPIOs
     */
    public boolean usesRP1() {
        return usesRP1;
    }
}
