package io.github.jaroslawdabrowski.pojo2md;

import io.github.jaroslawdabrowski.pojo2md.annotation.Paragraph;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownMapperParagraphTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class StringParagraphPojo {
        @Paragraph
        String text = "Hello world";
    }

    static class MarkdownParagraphPojo {
        @Paragraph
        Markdown content = Markdown.of().b("bold").t(" text");
    }

    static class LineBreakPojo {
        @Paragraph
        String text = "line one\nline two";
    }

    static class TwoParagraphsPojo {
        @Paragraph
        String first = "first";
        @Paragraph
        String second = "second";
    }

    @Test
    void stringParagraph() {
        assertThat(mapper.writeValueAsString(new StringParagraphPojo())).isEqualTo("Hello world");
    }

    @Test
    void markdownParagraph() {
        assertThat(mapper.writeValueAsString(new MarkdownParagraphPojo())).isEqualTo("**bold** text");
    }

    @Test
    void lineBreakNormalized() {
        assertThat(mapper.writeValueAsString(new LineBreakPojo())).isEqualTo("line one  \nline two");
    }

    @Test
    void declarationOrderRespected() {
        assertThat(mapper.writeValueAsString(new TwoParagraphsPojo()))
                .isEqualTo("""
                        first

                        second""");
    }
}
