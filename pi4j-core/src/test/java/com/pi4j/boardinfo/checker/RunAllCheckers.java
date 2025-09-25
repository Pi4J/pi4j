package com.pi4j.boardinfo.checker;

public class RunAllCheckers {

    public static void main(String[] args) {
        var i2c = I2CChecker.detectI2C();
        System.out.println("I2C:\n" + i2c);
    }
}
