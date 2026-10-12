---
type: C4 Component
title: Pi4J
status: stable
groma:
  id: pi4j-pi4j
  parent: pi4j-core
  code:
    - scanner: java
      file: pi4j-core/src/main/java/com/pi4j/Pi4J.java
description: Top-level entry point for building a Pi4J runtime context
---

Static factory used by applications to construct a Context, either with a default auto-detected plugin set or a hand-assembled one via ContextBuilder.
