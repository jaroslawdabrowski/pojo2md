package io.github.jaroslawdabrowski.pojo2md.builder;

import io.github.jaroslawdabrowski.pojo2md.model.Renderable;
import io.github.jaroslawdabrowski.pojo2md.model.Segment;
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

    public Markdown c(String text) {
        segments.add(new Segment.CodeSegment(text));
        return this;
    }

    public Markdown h(String text) {
        segments.add(new Segment.HighlightSegment(text));
        return this;
    }

    public Markdown s(String text) {
        segments.add(new Segment.StrikethroughSegment(text));
        return this;
    }

    public Markdown link(String text, String url) {
        segments.add(new Segment.LinkSegment(text, url, null));
        return this;
    }

    public Markdown link(String text, String url, String title) {
        segments.add(new Segment.LinkSegment(text, url, title));
        return this;
    }

    public Markdown autoLink(String url) {
        segments.add(new Segment.AutoLinkSegment(url));
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
