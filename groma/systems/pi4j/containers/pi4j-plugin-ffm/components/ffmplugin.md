---
type: C4 Component
title: FFMPlugin
status: stable
groma:
  id: ffmplugin
  parent: pi4j-plugin-ffm
  code:
    - scanner: java
      file: plugins/pi4j-plugin-ffm/src/main/java/com/pi4j/plugin/ffm/FFMPlugin.java
description: Registers the FFM provider implementations with Pi4J's plugin loader
---

The Plugin entry point (SPI-discovered via java.util.ServiceLoader) that the pi4j-core extension mechanism loads to register this module's GPIO, I2C, SPI, PWM, and parallel-port providers with a Context.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [plugins/pi4j-plugin-ffm/src/main/java/com/pi4j/plugin/ffm/FFMPlugin.java](../../../../../../plugins/pi4j-plugin-ffm/src/main/java/com/pi4j/plugin/ffm/FFMPlugin.java) | [pi4j-core/src/main/java/com/pi4j/extension/impl/DefaultPluginService.java](../../../../../../pi4j-core/src/main/java/com/pi4j/extension/impl/DefaultPluginService.java) | Registers providers | ServiceLoader |
