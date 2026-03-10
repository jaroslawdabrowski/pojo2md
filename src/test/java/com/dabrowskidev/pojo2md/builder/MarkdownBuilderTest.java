package com.dabrowskidev.pojo2md.builder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownBuilderTest {

    @Test
    void plainText() {
        assertThat(Markdown.of().t("hello").render()).isEqualTo("hello");
    }

    @Test
    void boldText() {
        assertThat(Markdown.of().b("bold").render()).isEqualTo("**bold**");
    }

    @Test
    void italicText() {
        assertThat(Markdown.of().i("italic").render()).isEqualTo("*italic*");
    }

    @Test
    void newLine() {
        assertThat(Markdown.of().newLine().render()).isEqualTo("  \n");
    }

    @Test
    void newParagraph() {
        assertThat(Markdown.of().newParagraph().render()).isEqualTo("\n\n");
    }

    @Test
    void chain() {
        assertThat(Markdown.of().b("bold").t(" and ").i("italic").render())
                .isEqualTo("**bold** and *italic*");
    }

    @Test
    void blockquoteLevel1() {
        assertThat(Markdown.of().t("text").blockquote().render()).isEqualTo("> text");
    }

    @Test
    void blockquoteLevel2() {
        assertThat(Markdown.of().t("text").blockquote(2).render()).isEqualTo(">> text");
    }

    @Test
    void contentAfterBlockquoteIsOutside() {
        assertThat(Markdown.of().t("inside").blockquote().t(" outside").render())
                .isEqualTo("> inside outside");
    }

    @Test
    void orderedListStrings() {
        assertThat(Markdown.of().orderedList(List.of("alpha", "beta", "gamma")).render())
                .isEqualTo("""
                        1. alpha
                        2. beta
                        3. gamma""");
    }

    @Test
    void unorderedListStrings() {
        assertThat(Markdown.of().unorderedList(List.of("a", "b")).render())
                .isEqualTo("""
                        - a
                        - b""");
    }

    @Test
    void orderedListMarkdownItems() {
        List<Markdown> items = List.of(
                Markdown.of().b("one"),
                Markdown.of().i("two")
        );
        assertThat(Markdown.of().orderedList(items).render())
                .isEqualTo("""
                        1. **one**
                        2. *two*""");
    }

    @Test
    void toStringDelegatesToRender() {
        Markdown md = Markdown.of().t("hi");
        assertThat(md.toString()).isEqualTo(md.render());
    }
}
