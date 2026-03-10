package com.dabrowskidev.pojo2md.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public sealed interface Segment permits
        Segment.PlainSegment,
        Segment.BoldSegment,
        Segment.ItalicSegment,
        Segment.NewLineSegment,
        Segment.NewParagraphSegment,
        Segment.BlockquoteSegment,
        Segment.OrderedListSegment,
        Segment.UnorderedListSegment {

    String render();

    record PlainSegment(String text) implements Segment {
        public String render() {
            return text;
        }
    }

    record BoldSegment(String text) implements Segment {
        public String render() {
            return "**" + text + "**";
        }
    }

    record ItalicSegment(String text) implements Segment {
        public String render() {
            return "*" + text + "*";
        }
    }

    record NewLineSegment() implements Segment {
        public String render() {
            return "  \n";
        }
    }

    record NewParagraphSegment() implements Segment {
        public String render() {
            return "\n\n";
        }
    }

    record BlockquoteSegment(int level, List<Segment> inner) implements Segment {
        public BlockquoteSegment {
            inner = List.copyOf(inner);
        }

        public String render() {
            String prefix = ">".repeat(level) + " ";
            String innerRendered = inner.stream()
                    .map(Segment::render)
                    .collect(Collectors.joining());
            return Arrays.stream(innerRendered.split("\n", -1))
                    .map(line -> prefix + line)
                    .collect(Collectors.joining("\n"));
        }
    }

    record OrderedListSegment(List<?> items) implements Segment {
        public OrderedListSegment {
            items = List.copyOf(items);
        }

        public String render() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append("\n");
                sb.append(i + 1).append(". ").append(renderItem(items.get(i)));
            }
            return sb.toString();
        }
    }

    record UnorderedListSegment(List<?> items) implements Segment {
        public UnorderedListSegment {
            items = List.copyOf(items);
        }

        public String render() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append("\n");
                sb.append("- ").append(renderItem(items.get(i)));
            }
            return sb.toString();
        }
    }

    private static String renderItem(Object item) {
        return switch (item) {
            case Renderable r -> r.render();
            case String s -> s;
            default -> item.toString();
        };
    }
}
