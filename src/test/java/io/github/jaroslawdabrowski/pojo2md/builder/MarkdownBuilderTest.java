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
    void codeText() {
        assertThat(Markdown.of().c("code").render()).isEqualTo("`code`");
    }

    @Test
    void link() {
        assertThat(Markdown.of().link("Duck Duck Go", "https://duckduckgo.com").render())
                .isEqualTo("[Duck Duck Go](https://duckduckgo.com)");
    }

    @Test
    void linkWithTitle() {
        assertThat(Markdown.of().link("Duck Duck Go", "https://duckduckgo.com", "The best search engine").render())
                .isEqualTo("[Duck Duck Go](https://duckduckgo.com \"The best search engine\")");
    }

    @Test
    void autoLink() {
        assertThat(Markdown.of().autoLink("https://www.markdownguide.org").render())
                .isEqualTo("<https://www.markdownguide.org>");
    }

    @Test
    void autoLinkEmail() {
        assertThat(Markdown.of().autoLink("fake@example.com").render())
                .isEqualTo("<fake@example.com>");
    }

    @Test
    void chain() {
        assertThat(Markdown.of().b("Visit").t(" ").link("pojo2md", "https://github.com/jaroslawdabrowski/pojo2md").render())
                .isEqualTo("**Visit** [pojo2md](https://github.com/jaroslawdabrowski/pojo2md)");
    }

    @Test
    void toStringDelegatesToRender() {
        Markdown md = Markdown.of().t("hi");
        assertThat(md.toString()).isEqualTo(md.render());
    }
}
