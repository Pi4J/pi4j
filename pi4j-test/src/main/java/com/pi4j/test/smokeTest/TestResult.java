package com.pi4j.test.smokeTest;

public record TestResult(String name, boolean success, String message) {
    public String getLogOutput() {
        return "Test " + name + "\n"
            + (success ? "PASSED" : "FAILED") + "\n"
            + message + "\n"
            + "==============================\n";
    }
}
