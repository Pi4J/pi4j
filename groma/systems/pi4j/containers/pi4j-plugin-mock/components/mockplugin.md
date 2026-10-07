---
type: C4 Component
title: MockPlugin
status: stable
groma:
  id: mockplugin
  parent: pi4j-plugin-mock
  code:
    - scanner: java
      file: plugins/pi4j-plugin-mock/src/main/java/com/pi4j/plugin/mock/MockPlugin.java
      symbol: MockPlugin
description: Registers the mock provider implementations with Pi4J's plugin loader
---

The Plugin entry point loaded via the extension mechanism to register this module's simulated digital I/O, I2C, SPI, and PWM providers with a Context, for development and testing without real hardware.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [plugins/pi4j-plugin-mock/src/main/java/com/pi4j/plugin/mock/MockPlugin.java](../../../../../../plugins/pi4j-plugin-mock/src/main/java/com/pi4j/plugin/mock/MockPlugin.java) | [pi4j-core/src/main/java/com/pi4j/extension/impl/DefaultPluginService.java](../../../../../../pi4j-core/src/main/java/com/pi4j/extension/impl/DefaultPluginService.java) | Registers providers | ServiceLoader |
