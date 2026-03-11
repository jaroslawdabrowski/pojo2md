package com.dabrowskidev.pojo2md.builder;

import com.dabrowskidev.pojo2md.model.Renderable;
import com.dabrowskidev.pojo2md.model.Segment;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Markdown implements Renderable {

    private final List<Segment> segments = new ArrayList<>();

    public static Markdown of() {
        return new Markdown();
    }

    public Markdown t(String text) {
        segments.add(new Segment.PlainSegment(text));
        return this;
    }

    public Markdown b(String text) {
        segments.add(new Segment.BoldSegment(text));
        return this;
    }

    public Markdown i(String text) {
        segments.add(new Segment.ItalicSegment(text));
        return this;
    }

    public Markdown newLine() {
        segments.add(new Segment.NewLineSegment());
        return this;
    }

    @Override
    public String render() {
        return segments.stream()
                .map(Segment::render)
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return render();
    }
}
