package com.dabrowskidev.pojo2md.model;

public sealed interface Segment permits
        Segment.PlainSegment,
        Segment.BoldSegment,
        Segment.ItalicSegment,
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

    record NewLineSegment() implements Segment {
        public String render() {
            return "  \n";
        }
    }
}
