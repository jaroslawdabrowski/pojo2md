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
- Feel free to add Apache Commons or other utility libraries to pom.xml when useful

## Code conventions

- Single class must not exceed 200 lines of code

## Architecture

```
com.dabrowskidev.pojo2md
├── ObjectMdMapper          Entry point — writeValueAsString(Object)
├── annotation/             @Heading, @HeadingValue, @Paragraph, @BlockQuote, @OrderedList, @UnorderedList
├── builder/
│   └── Markdown            Fluent builder and POJO field type; implements Renderable
├── model/
│   ├── Renderable          Interface implemented by Markdown to avoid circular deps with Segment
│   └── Segment             Sealed interface with all inline segment types as nested records
├── render/
│   ├── FieldElement        record(field, annotation, declarationIndex, sortKey)
│   ├── ElementRenderer     Dispatches FieldElement → rendered String per annotation type
│   └── HeadingResolver     Resolves heading text for @Heading (explicit value / String field / @HeadingValue)
└── exception/
    └── MappingException    Unchecked; wraps reflection errors and validation failures
```

### Rendering pipeline (ObjectMdMapper)
1. `getDeclaredFields()` — preserves field declaration order
2. Per field: detect single recognized annotation (>1 → `MappingException`)
3. Sort by field declaration index
5. Render each via `ElementRenderer`, join blocks with `\n\n`

### Markdown fluent builder
`Markdown.of().b("bold").t(" text").i("italic")` — each method appends a `Segment`.
`.blockquote(level)` wraps all previously accumulated segments into a `BlockquoteSegment` and resets the list.
`.orderedList(List<?>)` / `.unorderedList(List<?>)` accept `List<String>` or `List<Markdown>`.

### Supported annotations
| Annotation | Field types | Key attributes |
|---|---|---|
| `@Heading` | any | `level` (1–6), `value` (optional) |
| `@HeadingValue` | String | marker; used inside nested classes for `@Heading` resolution |
| `@Paragraph` | String, Markdown | — |
| `@BlockQuote` | String, Markdown | `level` (default 1) |
| `@OrderedList` | List\<String\>, List\<Markdown\> | — |
| `@UnorderedList` | List\<String\>, List\<Markdown\> | — |
