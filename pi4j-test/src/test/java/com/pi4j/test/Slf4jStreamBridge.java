package com.pi4j.test;

import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Slf4jStreamBridge {

    public static PrintStream createPrintStream(Logger logger) {
        return new PrintStream(new OutputStream() {
            private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    String line = bos.toString(StandardCharsets.UTF_8);
                    // Filter out trailing carriage returns if present
                    if (line.endsWith("\r")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    logger.info(line);
                    bos.reset();
                } else {
                    bos.write(b);
                }
            }
        }, true); // auto-flush enabled
    }
}
