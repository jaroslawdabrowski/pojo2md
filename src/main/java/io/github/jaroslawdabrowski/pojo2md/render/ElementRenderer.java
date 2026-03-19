package io.github.jaroslawdabrowski.pojo2md.render;

import io.github.jaroslawdabrowski.pojo2md.annotation.*;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;
import io.github.jaroslawdabrowski.pojo2md.exception.MappingException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ElementRenderer {

    private final HeadingResolver headingResolver = new HeadingResolver();
    private final TableRenderer tableRenderer = new TableRenderer();
    private final Function<Object, String> nestedRenderer;

    public ElementRenderer(Function<Object, String> nestedRenderer) {
        this.nestedRenderer = nestedRenderer;
    }

    public String render(FieldElement element, Object value) {
        StringBuilder result = new StringBuilder();

        if (element.heading() != null) {
            result.append("#".repeat(element.heading().level()))
                    .append(" ")
                    .append(headingResolver.resolve(element.heading()));
        }

        String content = renderContent(element, value);
        if (!content.isBlank()) {
            if (!result.isEmpty()) result.append("\n\n");
            result.append(content);
        }

        return result.toString();
    }

    private String renderContent(FieldElement element, Object value) {
        if (element.contentAnnotation() == null) {
            return renderByFieldType(value);
        }
        return switch (element.contentAnnotation()) {
            case Paragraph p -> renderParagraph(value);
            case BlockQuote bq -> renderBlockQuote(value, bq.level());
            case OrderedList ol -> renderOrderedList(value, element);
            case UnorderedList ul -> renderUnorderedList(value, element);
            case Section s -> renderNested(value);
            case Table t -> tableRenderer.render(element.field(), value);
            default -> throw new MappingException("Unknown annotation type: " + element.contentAnnotation());
        };
    }

    private String renderByFieldType(Object value) {
        if (value == null) return "";
        return switch (value) {
            case String s -> normalizeLineBreaks(s);
            case Markdown m -> m.render();
            default -> renderNested(value);
        };
    }

    private String renderNested(Object value) {
        if (value == null) return "";
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(nestedRenderer)
                    .collect(Collectors.joining("\n\n"));
        }
        if (value instanceof String || value instanceof Markdown) {
            throw new MappingException(
                    "@Section cannot be used on String or Markdown fields; use @Paragraph instead");
        }
        return nestedRenderer.apply(value);
    }

    private String renderParagraph(Object value) {
        if (value == null) return "";
        return switch (value) {
            case Markdown m -> m.render();
            case String s -> normalizeLineBreaks(s);
            default -> throw new MappingException(
                    "Expected String or Markdown, got: " + value.getClass().getSimpleName());
        };
    }

    private String renderBlockQuote(Object value, int level) {
        if (value == null) return "";
        String prefix = ">".repeat(level) + " ";
        String raw = switch (value) {
            case Markdown m -> m.render();
            case String s -> normalizeLineBreaks(s);
            default -> throw new MappingException(
                    "Expected String or Markdown, got: " + value.getClass().getSimpleName());
        };
        if (raw.isBlank()) return "";
        return Arrays.stream(raw.split("\n", -1))
                .map(line -> prefix + line)
                .collect(Collectors.joining("\n"));
    }

    private String renderOrderedList(Object value, FieldElement element) {
        List<?> items = asList(value, element);
        return IntStream.range(0, items.size())
                .mapToObj(i -> (i + 1) + ". " + renderItem(items.get(i)))
                .collect(Collectors.joining("\n"));
    }

    private String renderUnorderedList(Object value, FieldElement element) {
        List<?> items = asList(value, element);
        return items.stream()
                .map(item -> "- " + renderItem(item))
                .collect(Collectors.joining("\n"));
    }

    private List<?> asList(Object value, FieldElement element) {
        String annotation = "@" + element.contentAnnotation().annotationType().getSimpleName();
        String field = element.field().getName();
        if (value == null) {
            throw new MappingException("Field '" + field + "' annotated with " + annotation + " is null");
        }
        if (!(value instanceof List<?> list)) {
            throw new MappingException("Field '" + field + "' annotated with " + annotation + " must be of type List");
        }
        return list;
    }

    private String renderItem(Object item) {
        if (item == null) return "";
        return switch (item) {
            case Markdown m -> m.render();
            case String s -> s;
            default -> item.toString();
        };
    }

    static String normalizeLineBreaks(String s) {
        return s.replace("\n", "  \n");
    }
}
