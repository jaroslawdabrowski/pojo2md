package io.github.jaroslawdabrowski.pojo2md.model;

public sealed interface Segment permits
        Segment.PlainSegment,
        Segment.BoldSegment,
        Segment.ItalicSegment,
        Segment.CodeSegment,
        Segment.LinkSegment,
        Segment.AutoLinkSegment,
        Segment.NewLineSegment {

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

    record CodeSegment(String text) implements Segment {
        public String render() {
            return "`" + text + "`";
        }
    }

    record LinkSegment(String text, String url, String title) implements Segment {
        public String render() {
            if (title == null || title.isBlank()) return "[" + text + "](" + url + ")";
            return "[" + text + "](" + url + " \"" + title + "\")";
        }
    }

    record AutoLinkSegment(String url) implements Segment {
        public String render() {
            return "<" + url + ">";
        }
    }

    record NewLineSegment() implements Segment {
        public String render() {
            return "  \n";
        }
    }
}
