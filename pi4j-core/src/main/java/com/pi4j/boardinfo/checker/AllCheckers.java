package com.pi4j.boardinfo.checker;

import java.util.List;

public class AllCheckers {

    private AllCheckers() {
        // Hide constructor
    }

    public static List<CheckerResult> detect() {
        return List.of(
            //GPIOChecker.detect(),
            I2CChecker.detect()
            //PWMChecker.detect(),
            //SPIChecker.detect(),
            //SerialChecker.detect()
        );
    }

    public static void main(String[] args) {
        AllCheckers.detect()
            .forEach(c -> System.out.println(c.logOutput()
                + "\n\n-------------------------------------------------------------------\n"));
    }
}
