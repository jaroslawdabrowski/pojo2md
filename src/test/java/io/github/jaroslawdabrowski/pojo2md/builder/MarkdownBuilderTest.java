package io.github.jaroslawdabrowski.pojo2md.builder;

import org.junit.jupiter.api.Test;

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
    void chain() {
        assertThat(Markdown.of().b("bold").t(" and ").i("italic").render())
                .isEqualTo("**bold** and *italic*");
    }

    @Test
    void toStringDelegatesToRender() {
        Markdown md = Markdown.of().t("hi");
        assertThat(md.toString()).isEqualTo(md.render());
    }
}
