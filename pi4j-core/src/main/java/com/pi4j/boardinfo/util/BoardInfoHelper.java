package com.pi4j.boardinfo.util;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class BoardInfoHelper {

    private static final Logger logger = LoggerFactory.getLogger(BoardInfoHelper.class);

    private static final BoardInfoHelper instance;
    private BoardInfo boardInfo;

    static {
        instance = new BoardInfoHelper();
    }

    private BoardInfoHelper() {
        var os = new OperatingSystem(System.getProperty("os.name"), System.getProperty("os.version"),
            System.getProperty("os.arch"));
        logger.info("Detected OS: {}", os);

        var java = new JavaInfo(System.getProperty("java.version"), System.getProperty("java.runtime.version"),
            System.getProperty("java.vendor"), System.getProperty("java.vendor.version"));
        logger.info("Detected Java: {}", java);

        // Example output: c03111
        var boardVersionCode = getBoardVersionCode();
        try {
            var boardModelByBoardCode = BoardModel.getByBoardCode(boardVersionCode);
            if (boardModelByBoardCode != BoardModel.UNKNOWN) {
                logger.info("Detected board type {} by code: {}", boardModelByBoardCode.name(), boardVersionCode);
                this.boardInfo = new BoardInfo(boardModelByBoardCode, os, java);
                return;
            }
        } catch (Exception e) {
            logger.warn("Could not detect the board type for code {}: {}", boardVersionCode, e.getMessage());
        }

        // Example output: Raspberry Pi 4 Model B Rev 1.1
        var boardName = getBoardName();
        var boardModelByBoardName = BoardModel.getByBoardName(boardName);
        if (boardModelByBoardName != BoardModel.UNKNOWN) {
            logger.info("Detected board type {} by name: {}", boardModelByBoardName.name(), boardName);
            this.boardInfo = new BoardInfo(boardModelByBoardName, os, java);
            return;
        }

        // Maybe there are other ways how a board can be detected?
        // If so, this method can be further extended...
        logger.warn("Sorry, could not detect the board type");
        this.boardInfo = new BoardInfo(BoardModel.UNKNOWN, os, java);
    }

    public static BoardInfo current() {
        return instance.boardInfo;
    }

    /**
     * Flag indicating that the board is using the RP1 chip for GPIO.
     * <a href="https://www.raspberrypi.com/documentation/microcontrollers/rp1.html">https://www.raspberrypi.com/documentation/microcontrollers/rp1.html</a>
     * @return
     */
    public static boolean usesRP1() {
        return instance.boardInfo.getBoardModel() == BoardModel.MODEL_5_B;
    }

    public static boolean runningOnRaspberryPi() {
        return instance.boardInfo.getBoardModel() != BoardModel.UNKNOWN;
    }

    public static boolean is32bit() {
        return !is64bit();
    }

    public static boolean is64bit() {
        return System.getProperty("sun.arch.data.model").equals("64");
    }

    public static String getBoardVersionCode() {
        var output = getCommandOutput("cat /proc/cpuinfo | grep 'Revision' | awk '{print $3}'");
        if (output.isSuccess()) {
            return output.getOutputMessage();
        }
        logger.error("Could not get the board version code: {}", output.getErrorMessage());
        return "";
    }

    public static String getBoardName() {
        var output = getCommandOutput("cat /proc/device-tree/model");
        if (output.isSuccess()) {
            return output.getOutputMessage();
        }
        logger.error("Could not get the board name: {}", output.getErrorMessage());
        return "";
    }

    public static JvmMemory getJvmMemory() {
        return new JvmMemory(Runtime.getRuntime());
    }

    public static BoardReading getBoardReading() {
        return new BoardReading(
            getCommandOutput("cat /proc/device-tree/model").getOutputMessage(),
            // https://raspberry-projects.com/pi/command-line/detect-rpi-hardware-version
            getCommandOutput("cat /proc/cpuinfo | grep 'Revision' | awk '{print $3}'").getOutputMessage(),
            // https://linuxhint.com/commands-for-hardware-information-raspberry-pi/
             getCommandOutput("vcgencmd measure_temp").getOutputMessage(),
            getCommandOutput("uptime").getOutputMessage(),
            // https://linuxhint.com/find-hardware-information-raspberry-pi/
            getCommandOutput("vcgencmd measure_volts").getOutputMessage(),
            // https://www.baeldung.com/linux/total-physical-memory
            getCommandOutput("cat /proc/meminfo | head -n 1").getOutputMessage()
        );
    }

    private static class CommandResult {
        private final boolean success;
        private final String outputMessage;
        private final String errorMessage;

        public CommandResult(boolean success, String outputMessage, String errorMessage) {
            this.success = success;
            this.outputMessage = outputMessage;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOutputMessage() {
            return outputMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static CommandResult getCommandOutput(String command) {
        boolean finished = false;
        String outputMessage = "";
        String errorMessage = "";

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);

        try {
            Process process = builder.start();

            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            outputMessage = readStream(inputStream);
            errorMessage = readStream(errorStream);

            finished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();

            if (!finished) {
                process.destroyForcibly();
            }
        } catch (IOException ex) {
            errorMessage = "IOException: " + ex.getMessage();
        } catch (InterruptedException ex) {
            errorMessage = "InterruptedException: " + ex.getMessage();
        }

        if (!finished || !errorMessage.isEmpty()) {
            logger.error("Could not execute '{}' to detect the board model: {}", command, errorMessage);
            return new CommandResult(false, outputMessage, errorMessage);
        }

        return new CommandResult(true, outputMessage, errorMessage);
    }

    private static String readStream(InputStream inputStream) {
        StringBuilder rt = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                rt.append(line);
            }
        } catch (Exception ex) {
            rt.append("ERROR: ").append(ex.getMessage());
        }
        return rt.toString();
    }
}
