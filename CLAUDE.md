# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java library for generating Markdown from POJOs (similar to how Jackson generates JSON).

- **Group ID:** `com.dabrowskidev`
- **Artifact ID:** `pojo-to-markdown`
- **Main package:** `com.dabrowskidev.pojo2md`

## Commands

```bash
# Build
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClass

# Run a single test method
./mvnw test -Dtest=MyTestClass#myMethod
```

## Stack

- Java 21
- JUnit Jupiter 5.12.0
- Maven 3.9.9 (via wrapper)
