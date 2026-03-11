package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.BlockQuote;
import com.dabrowskidev.pojo2md.annotation.Paragraph;
import com.dabrowskidev.pojo2md.builder.Markdown;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownMapperBlockQuoteTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class Level1Pojo {
        @BlockQuote(level = 1)
        String text = "quote";
    }

    static class Level2Pojo {
        @BlockQuote(level = 2)
        String text = "deep";
    }

    static class MultiLinePojo {
        @BlockQuote(level = 1)
        String text = "line one\nline two";
    }

    static class MarkdownBlockQuotePojo {
        @BlockQuote(level = 1)
        Markdown content = Markdown.of().b("bold quote");
    }

    static class MixedOrderPojo {
        @Paragraph
        String para = "paragraph";
        @BlockQuote(level = 1)
        String quote = "quote";
    }

    @Test
    void level1BlockQuote() {
        assertThat(mapper.writeValueAsString(new Level1Pojo())).isEqualTo("> quote");
    }

    @Test
    void level2BlockQuote() {
        assertThat(mapper.writeValueAsString(new Level2Pojo())).isEqualTo(">> deep");
    }

    @Test
    void multiLineEachLinePrefixed() {
        assertThat(mapper.writeValueAsString(new MultiLinePojo()))
                .isEqualTo("> line one  \n> line two");
    }

    @Test
    void markdownBlockQuote() {
        assertThat(mapper.writeValueAsString(new MarkdownBlockQuotePojo()))
                .isEqualTo("> **bold quote**");
    }

    @Test
    void paragraphBeforeBlockQuoteByDeclarationOrder() {
        assertThat(mapper.writeValueAsString(new MixedOrderPojo()))
                .isEqualTo("""
                        paragraph

                        > quote""");
    }
}
