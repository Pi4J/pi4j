---
type: C4 Container
title: Plugin FFM
status: stable
groma:
  id: pi4j-plugin-ffm
  parent: pi4j
  technology: Java, JDK Foreign Function & Memory API
description: Default Pi4J provider backed by the Java Foreign Function & Memory API
---

Implements pi4j-core's provider interfaces (digital I/O, I2C, SPI, PWM, parallel port) by calling into the Linux GPIO character device, I2C/SMBus, and ioctl interfaces directly through the JDK's FFM (Panama) API, without JNI or native glue code. Also exposes a lower-level Pi4JApi facade and native line/permission/polling primitives used by the providers.
