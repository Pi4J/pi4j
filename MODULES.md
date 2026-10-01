# Java Platform Module System (JPMS)

Pi4J no longer ships `module-info.java` files. This note explains why, and how to
get JPMS module names back if you need them.

## What changed

Up to 5.0.0-SNAPSHOT, `pi4j-core`, `pi4j-test`, `pi4j-plugin-ffm` and
`pi4j-plugin-mock` each had a `module-info.java`, making them explicit JPMS
modules. Those files have been removed.

## Why

- **No one was maintaining them.** They were added once in 2019 and only
  touched incidentally since, as a side effect of unrelated refactors — not
  because anyone was keeping `exports`/`opens`/`requires`/`uses` in sync with
  the code. `pi4j-test`'s module even still required `jdk.incubator.vector`,
  which nothing in the codebase uses.
- **Little real benefit.** `pi4j-core` exported almost its entire public
  package surface, so the "strong encapsulation" JPMS offers wasn't actually
  being exercised. Nothing in the build uses `jlink` to produce a custom
  runtime image either.
- **IDE pain.** Test sources had no `module-info.java` of their own, so they
  live in the unnamed module against a modular main module. Maven's
  compiler/surefire plugins paper over that automatically, but IntelliJ's
  generated run configurations don't: every "Run Test" via Maven import
  defaulted to "use module classpath", which fails, requiring contributors to
  manually uncheck it on every run config (and a fresh one gets created each
  time you run via the context menu).

## What you lose

If you consume Pi4J from your own JPMS-modular application (module path, not
classpath) — for example to build a `jlink` custom runtime — the published
jars are now **automatic modules** instead of **explicit modules**. You get a
stable, predictable module name (see below), but no compiler-enforced
`exports`/`opens`/`uses`-`provides` from Pi4J's side.

## Module names

Each published jar still declares an `Automatic-Module-Name` in its manifest,
so the module name stays stable even without `module-info.java`:

| Artifact             | Module name            |
|-----------------------|-------------------------|
| `pi4j-core`           | `com.pi4j`              |
| `pi4j-test`           | `com.pi4j.test`         |
| `pi4j-plugin-ffm`     | `com.pi4j.plugin.ffm`   |
| `pi4j-plugin-mock`    | `com.pi4j.plugin.mock`  |

## If you need full JPMS support again

Add a `module-info.java` back to the relevant module, mirroring the
`Automatic-Module-Name` as the module name, and move the test sources into
their own named test module (or keep them unnamed and accept the IDE
workaround). Please also update this file and take ownership of keeping the
module declarations correct as the code evolves — that's the part that broke
down last time.
