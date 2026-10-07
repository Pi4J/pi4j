---
type: C4 Container
title: Core
status: draft
groma:
  id: pi4j-core
  parent: pi4j
  technology: Java
description: Pi4J's core API and default runtime
---

Defines the public Pi4J API (GPIO digital and analog I/O, I2C, SPI, PWM, serial/parallel port, PCA9685, board info and detection, context, plugin extension loading, configuration, and events) and the default in-process implementation that backs it. Provider plugins (pi4j-plugin-ffm, pi4j-plugin-mock) implement the interfaces this container defines and are discovered through its plugin/extension loading mechanism.
