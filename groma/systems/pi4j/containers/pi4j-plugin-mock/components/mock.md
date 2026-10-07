---
type: C4 Component
title: Mock
status: stable
groma:
  id: mock
  parent: pi4j-plugin-mock
  code:
    - scanner: java
      file: plugins/pi4j-plugin-mock/src/main/java/com/pi4j/plugin/mock/Mock.java
      symbol: Mock
description: Shared mock provider identity and naming constants
---

Constants (provider id, name) used across this module's digital I/O, I2C, SPI, and PWM mock providers.
