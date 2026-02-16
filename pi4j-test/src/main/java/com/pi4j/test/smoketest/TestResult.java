package com.pi4j.test.smoketest;

import org.slf4j.Logger;

public record TestResult(String name, boolean success, String message) {
    public String log(Logger logger) {
        logger.info("Test: {}", name);
        logger.info("\t{}", success ? "PASSED" : "FAILED");
        logger.info("\t{}", message);
        logger.info("==============================================================");
    }
}
