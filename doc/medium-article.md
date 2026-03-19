# Jackson for Markdown: A Java Library for LLM-Ready Structured Prompts

Every Java developer building AI features writes the same boilerplate: pull data from a database, call `objectMapper.writeValueAsString()`, paste the JSON into a prompt, and fire it at an LLM. It works. But it's the equivalent of emailing a spreadsheet as a `.csv` when you could send a formatted PDF — technically the same data, but the presentation changes how well it's understood.

Research backs this up: prompt format measurably affects LLM accuracy and cost. And until recently, Java had no library for the better format.

**The short version:** pojo2md is an open-source Java library that serializes POJOs to Markdown using annotations, the same way Jackson serializes to JSON. One method call, type-safe, refactor-friendly.

---

## Why Your JSON Prompts Are Suboptimal

When you feed an LLM a medication order as JSON, you get something like this:

```json
{
  "orderId": "ORD-9182",
  "patient": "Sarah Chen",
  "status": "PENDING_REVIEW",
  "items": [
    { "sku": "MED-301", "description": "Blood pressure monitor", "qty": 2, "unitPrice": 1200.00 },
    { "sku": "MED-445", "description": "Pulse oximeter", "qty": 5, "unitPrice": 170.00 }
  ],
  "flags": ["HIGH_VALUE", "INSURANCE_REQUIRED", "FIRST_TIME_CUSTOMER"]
}
```

It's precise. It's also how machines talk to machines — not how documents talk to reasoning systems.

A 2024 paper on arXiv, [*Does Prompt Formatting Have Any Impact on LLM Performance?*](https://arxiv.org/html/2411.10541v1), tested plain text, Markdown, YAML, and JSON across multiple GPT models on reasoning and code generation benchmarks. The result: **GPT-4 scored 81.2% on a reasoning task with Markdown prompts versus 73.9% with JSON** — a 7.3-point gap. For code generation, the differences were even larger. Results may vary across model families, but the directional finding — that format matters — has been consistent across studies.

Token efficiency tells a separate but complementary story. A [benchmark on the OpenAI developer forum](https://community.openai.com/t/markdown-is-15-more-token-efficient-than-json/841742) converted identical data across formats and measured with tiktoken: **JSON used 13,869 tokens; Markdown used 11,612** — roughly 16% fewer tokens. At scale, that means lower API costs and more room within context windows.

The intuition follows naturally: LLMs are trained on vast quantities of human-written text. Markdown is how humans structure documents — headings, lists, tables, emphasis. When you want a model to *reason* about data rather than just parse it, presenting that data as a well-organized document aligns with the patterns the model learned during training.

To be clear, this applies to the *input* side — the context you provide. When you need structured data back from the model, JSON remains the right choice for output parsing.

---

## The Gap No One Filled

Java developers reaching for prompt generation today have three options:

1. **String concatenation** — fragile, no IDE support, breaks silently on refactoring
2. **Template engines** (FreeMarker, Velocity, Mustache) — templates drift out of sync with the data model; every field rename means two changes in two places
3. **Manual builder code** — verbose, no standard structure, every team invents their own conventions

None of these give you what Jackson gave us for JSON: a declarative, annotation-driven serialization model where the structure lives *on the data class*, co-located with the fields it describes.

Rename a field and your IDE refactors the annotation with it. Add a field without an annotation and it's silently skipped — a safe default. The output format is always visible at the class definition, not buried in a template file somewhere else in the project.

---

## Introducing pojo2md

**pojo2md** fills that gap. It serializes Java objects to Markdown using field annotations, following the same mental model as Jackson.

The API surface is one method:

```java
MarkdownMapper mapper = new MarkdownMapper();
String markdown = mapper.writeValueAsString(myPojo);
```

Fields without annotations are skipped — fully opt-in. Annotate what you want in the output; the rendering pipeline handles the rest.

```xml
<dependency>
    <groupId>io.github.jaroslawdabrowski</groupId>
    <artifactId>pojo2md</artifactId>
    <version>1.0.7</version>
</dependency>
```

GitHub: [github.com/jaroslawdabrowski/pojo2md](https://github.com/jaroslawdabrowski/pojo2md)

---

## The Annotation Model

If you've used Jackson annotations to control JSON output, this will feel familiar. Each pojo2md annotation maps to a Markdown block element:

| Annotation | Output |
| --- | --- |
| `@Heading(level=2, value="Title")` | `## Title` |
| `@Paragraph` | plain paragraph text |
| `@BlockQuote` | `> quoted text` |
| `@OrderedList` | `1.` numbered list |
| `@UnorderedList` | `-` bullet list |
| `@Section` | embedded nested POJO, no heading |
| `@Table` | full Markdown table from `List<RowPojo>` |
| `@Column(header="Name")` | table column on row POJO fields |

Annotations compose naturally. `@Heading` stacks with any content annotation — put both `@Heading(level=2, value="Items")` and `@Table` on the same field and you get a heading followed by the rendered table.

For inline formatting, the library provides a fluent `Markdown` builder:

```java
Markdown.of()
    .b("Patient:")    // bold
    .t(" Sarah Chen") // plain text
    .newLine()
    .b("Status:")
    .t(" Pending Review");
```

The builder also supports `.i()` for italic, `.c()` for inline code, `.s()` for strikethrough, and `.link()` for hyperlinks — everything you need for rich inline content without writing raw Markdown syntax.

---

## A Real-World Example: Medication Order Review

Consider a healthcare platform sending medication orders to an LLM for automated compliance review. The model needs to check for high-value thresholds, flag insurance requirements, and generate a risk summary.

Start by defining the data classes:

```java
class OrderItem {
    @Column(header = "SKU")
    String sku;

    @Column(header = "Description")
    String description;

    @Column(header = "Qty", alignment = Align.RIGHT)
    String quantity;

    @Column(header = "Unit Price", alignment = Align.RIGHT)
    String unitPrice;

    // constructor omitted for brevity
}

class MedicationOrder {

    @Heading(level = 1, value = "Medication Order")
    @Paragraph
    Markdown summary;

    @Heading(level = 2, value = "Order Items")
    @Table
    List<OrderItem> items;

    @Heading(level = 2, value = "Risk Flags")
    @UnorderedList
    List<String> flags;

    @Heading(level = 2, value = "Compliance Note")
    @BlockQuote
    String complianceNote;
}
```

Populate it with data and call `writeValueAsString()`:

```java
var order = new MedicationOrder();

order.summary = Markdown.of()
    .b("Order ID:").t(" ORD-9182").newLine()
    .b("Patient:").t(" Sarah Chen").newLine()
    .b("Status:").t(" Pending Review");

order.items = List.of(
    new OrderItem("MED-301", "Blood pressure monitor", "2", "$1,200.00"),
    new OrderItem("MED-445", "Pulse oximeter", "5", "$170.00")
);

order.flags = List.of(
    "HIGH_VALUE — total exceeds $4,000",
    "INSURANCE_REQUIRED — prior authorization needed",
    "FIRST_TIME_CUSTOMER — no previous order history"
);

order.complianceNote = "Requires pharmacist sign-off before dispatch.";

String prompt = mapper.writeValueAsString(order);
```

The output:

```markdown
# Medication Order

**Order ID:** ORD-9182
**Patient:** Sarah Chen
**Status:** Pending Review

## Order Items

| SKU | Description | Qty | Unit Price |
| --- | --- | ---: | ---: |
| MED-301 | Blood pressure monitor | 2 | $1,200.00 |
| MED-445 | Pulse oximeter | 5 | $170.00 |

## Risk Flags

- HIGH_VALUE — total exceeds $4,000
- INSURANCE_REQUIRED — prior authorization needed
- FIRST_TIME_CUSTOMER — no previous order history

## Compliance Note

> Requires pharmacist sign-off before dispatch.
```

Compare this to the JSON blob at the top of the article. The Markdown version communicates *structure and intent* simultaneously.

`## Risk Flags` tells the model that what follows is a set of warnings — not just a key named `flags` with an array value. The blockquote visually separates a directive from data. Right-aligned price columns hint at numeric content without explicit type annotations. These are the same structural signals that help a human skim a document — and they help an LLM reason about it too.

---

## What You Can Build With This

The pattern generalizes well beyond healthcare. Any domain where you send structured context to an LLM benefits:

- **Financial reports** — feed positions and P&L tables to a model for portfolio analysis or compliance checks
- **Customer support** — give the model a ticket history with escalation flags so it can draft a response or route the case
- **Legal review** — present clause summaries and risk annotations for automated contract analysis
- **Incident management** — format timelines, severity data, and runbook steps so the model can suggest next actions

In each case, the Java data model already exists. pojo2md adds annotations to describe *how that data should be presented to a model* — without creating a parallel representation in templates or string concatenation code.

The approach also makes prompts testable. When the Markdown structure is defined by annotations on a Java class, it can be code-reviewed, version-controlled, and unit-tested like any other serialization output. Assert the exact Markdown your POJO produces, just as you would assert a JSON payload. Prompt regressions become detectable before they reach production.

---

## Getting Started

Add the dependency, annotate your fields, call `writeValueAsString()`. No configuration, no plugins, no runtime dependencies beyond the library itself.

The [GitHub repository](https://github.com/jaroslawdabrowski/pojo2md) includes full documentation, the annotation reference, and examples covering nested POJOs, composed annotations, and table rendering.

If you're building AI features in Java and hand-crafting prompt strings today, this is the refactoring that pays for itself every time a data model changes. Your prompts deserve the same engineering rigor as your API contracts.

---

*Sources: [Does Prompt Formatting Have Any Impact on LLM Performance? (arXiv, 2024)](https://arxiv.org/html/2411.10541v1) · [Markdown is 15% more token efficient than JSON (OpenAI Developer Community)](https://community.openai.com/t/markdown-is-15-more-token-efficient-than-json/841742) · [Which Nested Data Format Do LLMs Understand Best? (ImprovingAgents)](https://www.improvingagents.com/blog/best-nested-data-format/) · [Boosting AI Performance: The Power of LLM-Friendly Content in Markdown (Webex Developers Blog)](https://developer.webex.com/blog/boosting-ai-performance-the-power-of-llm-friendly-content-in-markdown)*
