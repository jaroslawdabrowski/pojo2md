# pojo2md

A Java library for generating Markdown from plain Java objects — annotation-driven, like Jackson for JSON but for Markdown output.

Define your document structure as a plain Java class, annotate the fields, and call `writeValueAsString()`. The library handles the rest.

---

## Quick Start

```java
class Report {
    @Heading(level = 1, value = "Q1 Summary")
    @Paragraph
    String intro = "This report covers Q1 2026 performance.";

    @Heading(level = 2, value = "Key Metrics")
    @UnorderedList
    List<String> metrics = List.of(
        "Revenue: $1.2M (+12%)",
        "Active users: 45,000",
        "Churn: 2.3%"
    );
}

String md = new MarkdownMapper().writeValueAsString(new Report());
```

Output:

```markdown
# Q1 Summary

This report covers Q1 2026 performance.

## Key Metrics

- Revenue: $1.2M (+12%)
- Active users: 45,000
- Churn: 2.3%
```

---

## Installation

```xml
<dependency>
    <groupId>com.dabrowskidev</groupId>
    <artifactId>pojo2md</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Requires **Java 21+**.

---

## Annotations

### `@Heading`

Renders a Markdown heading before the field content. `value` is required and provides the static heading text.

```java
@Heading(level = 2, value = "Introduction")
@Paragraph
String intro = "Welcome to the docs.";
```

```markdown
## Introduction

Welcome to the docs.
```

**Standalone heading** — use a null field when you only want the heading with no content below:

```java
@Heading(level = 1, value = "My Document")
String dummy = null;
```

```markdown
# My Document
```

**Composable** — stack `@Heading` with any content annotation:

```java
@Heading(level = 2, value = "Steps")
@OrderedList
List<String> steps = List.of("Install", "Configure", "Run");
```

```markdown
## Steps

1. Install
2. Configure
3. Run
```

**Nested content** — `@Heading` alone on a POJO or list field renders the heading then the nested content (no `@Section` needed):

```java
@Heading(level = 2, value = "Agenda")
List<AgendaItem> items = List.of(...);
```

---

### `@Paragraph`

Renders a `String` or `Markdown` field as a paragraph block.

```java
@Paragraph
String description = "A plain text paragraph.";

@Paragraph
Markdown formatted = Markdown.of().b("Bold").t(" and ").i("italic").t(" text.");
```

```markdown
A plain text paragraph.

**Bold** and *italic* text.
```

---

### `@BlockQuote`

Renders a `String` or `Markdown` field as a block quote. Supports nesting via `level`.

```java
@BlockQuote
String note = "This is important.";

@BlockQuote(level = 2)
String nested = "Deeply nested quote.";
```

```markdown
> This is important.

>> Deeply nested quote.
```

---

### `@OrderedList` / `@UnorderedList`

Renders a `List<String>` or `List<Markdown>` as a numbered or bulleted list.

```java
@OrderedList
List<String> steps = List.of("First", "Second", "Third");

@UnorderedList
List<Markdown> items = List.of(
    Markdown.of().b("Alpha"),
    Markdown.of().b("Beta")
);
```

```markdown
1. First
2. Second
3. Third

- **Alpha**
- **Beta**
```

---

### `@Section`

Embeds a nested POJO or `List<POJO>` **without** a heading. Use this when you want to inline structured content directly.

```java
class Summary {
    @Paragraph
    String text = "Details below.";
}

class Document {
    @Section
    Summary summary = new Summary();
}
```

```markdown
Details below.
```

> When a heading is present, use `@Heading` alone — adding `@Section` is unnecessary.

---

## Inline Formatting with `Markdown`

The `Markdown` builder creates inline-formatted content for use with `@Paragraph`, `@BlockQuote`, and list fields.

```java
Markdown body = Markdown.of()
    .b("Bold")       // **Bold**
    .t(" normal ")   // normal text
    .i("italic")     // *italic*
    .newLine()       // soft line break (two spaces + \n)
    .t("next line");
```

| Method | Output |
|--------|--------|
| `.t("text")` | `text` |
| `.b("text")` | `**text**` |
| `.i("text")` | `*text*` |
| `.newLine()` | soft line break (`  \n`) |

---

## Nested POJOs

Annotated fields in nested POJOs are rendered recursively. Each nested object is processed using the same annotation-driven rules and the results are joined with blank lines.

```java
class Section {
    @Paragraph
    String body;

    @UnorderedList
    List<String> points;
}

class Document {
    @Heading(level = 2, value = "Overview")
    @Section
    Section overview = new Section();
}
```

---

## Full Example

A complete meeting document built from a nested object graph:

```java
class AgendaItem {
    @Paragraph
    Markdown title;

    @Heading(level = 4, value = "Participants")
    @UnorderedList
    List<String> participants;

    AgendaItem(String titleText, List<String> participants) {
        this.title = Markdown.of().b(titleText);
        this.participants = participants;
    }
}

class Meeting {
    @Heading(level = 1, value = "Q1 Planning Meeting")
    @Paragraph
    Markdown meta = Markdown.of()
            .b("Date:").t(" 2026-03-10").newLine()
            .b("Location:").t(" Conference Room A").newLine()
            .b("Duration:").t(" 60 minutes");

    @Heading(level = 2, value = "Participants")
    @UnorderedList
    List<String> participants = List.of(
            "Alice Smith — Product Manager",
            "Bob Jones — Engineering Lead",
            "Carol White — Design",
            "Dave Brown — QA"
    );

    @Heading(level = 2, value = "Agenda")
    List<AgendaItem> agendaItems = List.of(
            new AgendaItem("Status Update", List.of("Alice Smith", "Bob Jones")),
            new AgendaItem("Blockers", List.of("Carol White")),
            new AgendaItem("Q2 Roadmap", List.of("Alice Smith"))
    );

    @Heading(level = 3, value = "Action Items")
    @OrderedList
    List<String> actionItems = List.of(
            "Bob to finalize API documentation by March 12th",
            "Carol to share updated mockups in Slack"
    );

    @BlockQuote
    String nextMeeting = "Next meeting scheduled for March 17, 2026 at 10:00 AM";
}

String md = new MarkdownMapper().writeValueAsString(new Meeting());
```

Output:

```markdown
# Q1 Planning Meeting

**Date:** 2026-03-10
**Location:** Conference Room A
**Duration:** 60 minutes

## Participants

- Alice Smith — Product Manager
- Bob Jones — Engineering Lead
- Carol White — Design
- Dave Brown — QA

## Agenda

**Status Update**

#### Participants

- Alice Smith
- Bob Jones

**Blockers**

#### Participants

- Carol White

**Q2 Roadmap**

#### Participants

- Alice Smith

### Action Items

1. Bob to finalize API documentation by March 12th
2. Carol to share updated mockups in Slack

> Next meeting scheduled for March 17, 2026 at 10:00 AM
```

---

## Annotation Reference

| Annotation | Supported field types | Attributes |
|---|---|---|
| `@Heading` | any | `level` (1–6, default 1), `value` (required) |
| `@Paragraph` | `String`, `Markdown` | — |
| `@BlockQuote` | `String`, `Markdown` | `level` (default 1) |
| `@OrderedList` | `List<String>`, `List<Markdown>` | — |
| `@UnorderedList` | `List<String>`, `List<Markdown>` | — |
| `@Section` | any POJO, `List<POJO>` | — |

### Rules

- A field with **no annotation** is always skipped.
- A field may have **at most one** content annotation (`@Paragraph`, `@BlockQuote`, `@OrderedList`, `@UnorderedList`, `@Section`).
- `@Heading` may be **combined** with any content annotation.
- Fields are rendered in **declaration order**.
- Blocks are separated by a **blank line** (`\n\n`).

---

## Error Handling

All mapping errors throw `MappingException` (unchecked):

- Field has more than one content annotation
- `@OrderedList` / `@UnorderedList` used on a non-`List` field
- `@Paragraph` / `@BlockQuote` used on an unsupported type
