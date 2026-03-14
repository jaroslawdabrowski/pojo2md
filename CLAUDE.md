# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java library for generating Markdown from POJOs (similar to how Jackson generates JSON).

- **Group ID:** `io.github.jaroslawdabrowski`
- **Artifact ID:** `pojo2md`
- **Main package:** `io.github.jaroslawdabrowski.pojo2md`

## Commands

```bash
# Build and run full CI (tests + PMD + SpotBugs)
./mvnw verify -Pci

# Run all tests only
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClass

# Run a single test method
./mvnw test -Dtest=MyTestClass#myMethod
```

> **Note:** System default Java is 21 — no need to set `JAVA_HOME`. Just use `./mvnw` directly.

## Stack

- Java 21
- JUnit Jupiter 5.12.0
- Maven 3.9.9 (via wrapper)
- Feel free to add Apache Commons or other utility libraries to pom.xml when useful

## Code conventions

- Single class must not exceed 200 lines of code

## Architecture

```
io.github.jaroslawdabrowski.pojo2md
├── MarkdownMapper          Entry point — writeValueAsString(Object)
├── annotation/             @Heading, @Paragraph, @BlockQuote, @OrderedList, @UnorderedList, @Section
├── builder/
│   └── Markdown            Fluent inline builder; implements Renderable
├── model/
│   ├── Renderable          Interface implemented by Markdown to avoid circular deps with Segment
│   └── Segment             Sealed interface: PlainSegment, BoldSegment, ItalicSegment, NewLineSegment
├── render/
│   ├── FieldElement        record(field, heading, contentAnnotation, declarationIndex)
│   ├── ElementRenderer     Two-phase render: heading prefix + content dispatch
│   └── HeadingResolver     Returns heading.value()
└── exception/
    └── MappingException    Unchecked; wraps reflection errors and validation failures
```

### Rendering pipeline (MarkdownMapper)
1. `getDeclaredFields()` — preserves field declaration order
2. Per field: extract `@Heading` (optional) + one content annotation (optional); both together allowed
3. Field is included if it has `@Heading` OR a content annotation; otherwise skipped (no auto-detection)
4. Sort by field declaration index
5. Render each via `ElementRenderer`, join blocks with `\n\n`

### Markdown fluent builder
`Markdown.of().b("bold").t(" text").i("italic").newLine()` — each method appends an inline `Segment`.
Only inline formatting — no block-level methods (those belong to field-level annotations).

### Supported annotations

| Annotation | Field types | Key attributes | Notes |
|---|---|---|---|
| `@Heading` | any | `level` (1–6), `value` (required) | Renders heading from `value`; field content rendered below. Stackable with content annotations. |
| `@Paragraph` | String, Markdown | — | |
| `@BlockQuote` | String, Markdown | `level` (default 1) | |
| `@OrderedList` | List\<String\>, List\<Markdown\> | — | |
| `@UnorderedList` | List\<String\>, List\<Markdown\> | — | |
| `@Section` | POJO, List\<POJO\> | — | Embeds nested POJO(s) without a heading. Use `@Heading` alone when a heading is present. |

### @Heading semantics
- `value` is **required** — heading text always comes from the annotation, never from the field value
- `@Heading` alone on a null field → renders just the heading (no content below)
- `@Heading` on a String/Markdown field → renders heading, then field value below (if not null)
- `@Heading` on a POJO/List field → renders heading, then nested content below (no `@Section` needed)
- `@Heading` + content annotation (e.g. `@Heading + @UnorderedList`) → heading first, then annotation-driven content

### @Section semantics
- Use when you want to embed a nested POJO or `List<POJO>` **without** a heading
- `@Heading` alone is sufficient when a heading is present — `@Section` is redundant in that case
- Unannotated POJO/List fields are **always skipped**
