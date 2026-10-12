---
type: C4 Component
title: Main
status: stable
groma:
  id: main
  parent: pi4j-test
  code:
    - scanner: java
      file: pi4j-test/src/main/java/com/pi4j/test/Main.java
      symbol: Main
description: Runnable entry point for manual smoke testing on hardware
---

Wires up a Context with real hardware providers and runs the smoke-test cases interactively; the counterpart used for automated runs is the test provider doubles.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [pi4j-test/src/main/java/com/pi4j/test/Main.java](../../../../../../pi4j-test/src/main/java/com/pi4j/test/Main.java) | [pi4j-core/src/main/java/com/pi4j/Pi4J.java](../../../../../../pi4j-core/src/main/java/com/pi4j/Pi4J.java) | Builds runtime context | Java |
