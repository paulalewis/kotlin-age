# Abstract Game Engine

This is a Kotlin framework for creating turn based
domains and custom agents to play in those domains.

Domains:
- [backgammon](https://en.wikipedia.org/wiki/Backgammon)
- [biniax](https://en.wikipedia.org/wiki/Biniax)
- [connect4](https://en.wikipedia.org/wiki/Connect4)
- [havannah](https://en.wikipedia.org/wiki/Havannah)
- [hex](https://en.wikipedia.org/wiki/Hex_%28board_game%29)
- [yahtzee](https://en.wikipedia.org/wiki/Yahtzee)

Agents:
- random - agent randomly selects an action

## Setup

1. Install a JDK 21+ (for example via Homebrew: `brew install openjdk@21`)
2. The Gradle wrapper is included; no separate Gradle install is required

Point `JAVA_HOME` at JDK 21 if needed:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

## Build

```bash
./gradlew clean build
```

`build` compiles the library, runs the full test suite, and packages the JAR.

## Run Tests

```bash
./gradlew test
```

You should see each test listed as `PASSED` / `FAILED`, then a summary like:

```text
Test result: SUCCESS
Test summary: 137 tests, 137 succeeded, 0 failed, 0 skipped
```

Useful variants:

```bash
# Force tests to re-run even if Gradle thinks they are up to date
./gradlew cleanTest test
```

HTML report (after a test run):

```text
build/reports/tests/test/index.html
```
