package com.dabrowskidev.pojo2md.render;

import com.dabrowskidev.pojo2md.annotation.*;
import com.dabrowskidev.pojo2md.builder.Markdown;
import com.dabrowskidev.pojo2md.exception.MappingException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ElementRenderer {

    private final HeadingResolver headingResolver = new HeadingResolver();
    private final Function<Object, String> nestedRenderer;

    public ElementRenderer(Function<Object, String> nestedRenderer) {
        this.nestedRenderer = nestedRenderer;
    }

    public String render(FieldElement element, Object value) {
        if (element.isNested()) return renderNested(value);
        return switch (element.annotation()) {
            case Heading h -> renderHeading(element.field(), value, h);
            case Paragraph ignored -> renderParagraph(value);
            case BlockQuote bq -> renderBlockQuote(value, bq.level());
            case OrderedList ignored -> renderOrderedList(value, element);
            case UnorderedList ignored -> renderUnorderedList(value, element);
            default -> throw new MappingException("Unknown annotation type: " + element.annotation());
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
        return nestedRenderer.apply(value);
    }

    private String renderHeading(java.lang.reflect.Field field, Object value, Heading heading) {
        String text = headingResolver.resolve(field, value);
        return "#".repeat(heading.level()) + " " + text;
    }

    private String renderParagraph(Object value) {
        return switch (value) {
            case Markdown m -> m.render();
            case String s -> normalizeLineBreaks(s);
            default -> throw new MappingException(
                    "Expected String or Markdown, got: " + value.getClass().getSimpleName());
        };
    }

    private String renderBlockQuote(Object value, int level) {
        String prefix = ">".repeat(level) + " ";
        String raw = switch (value) {
            case Markdown m -> m.render();
            case String s -> normalizeLineBreaks(s);
            default -> throw new MappingException(
                    "Expected String or Markdown, got: " + value.getClass().getSimpleName());
        };
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
        if (!(value instanceof List<?> list)) {
            throw new MappingException(
                    "Field '" + element.field().getName() + "' annotated with @"
                    + element.annotation().annotationType().getSimpleName()
                    + " must be of type List");
        }
        return list;
    }

    private String renderItem(Object item) {
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
