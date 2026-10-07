---
type: C4 Container
title: Plugin Mock
status: draft
groma:
  id: pi4j-plugin-mock
  parent: pi4j
  technology: Java
description: Simulated GPIO/I2C/SPI/PWM provider for development and testing without real hardware
---

Implements Pi4J's provider interfaces (digital I/O, I2C, PWM, SPI) with in-memory simulated behavior instead of talking to real Raspberry Pi hardware, so applications and tests can run on any machine.
