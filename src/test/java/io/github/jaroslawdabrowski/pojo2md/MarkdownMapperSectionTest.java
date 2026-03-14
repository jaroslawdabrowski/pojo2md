package io.github.jaroslawdabrowski.pojo2md;

import io.github.jaroslawdabrowski.pojo2md.annotation.Heading;
import io.github.jaroslawdabrowski.pojo2md.annotation.Paragraph;
import io.github.jaroslawdabrowski.pojo2md.annotation.Section;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownMapperSectionTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class Inner {
        @Paragraph
        String text;

        Inner(String text) {
            this.text = text;
        }
    }

    static class WithSingleSection {
        @Section
        Inner inner = new Inner("hello from inner");
    }

    static class WithSectionList {
        @Section
        List<Inner> items = List.of(new Inner("first"), new Inner("second"));
    }

    static class WithHeadingAndSection {
        @Heading(level = 2, value = "Details")
        @Section
        Inner inner = new Inner("content here");
    }

    static class UnannotatedPojoIsSkipped {
        Inner inner = new Inner("should not render");
        @Paragraph
        String visible = "visible";
    }

    @Test
    void singleNestedPojoRenderedWithSection() {
        assertThat(mapper.writeValueAsString(new WithSingleSection()))
                .isEqualTo("hello from inner");
    }

    @Test
    void listOfNestedPojosRenderedWithSection() {
        assertThat(mapper.writeValueAsString(new WithSectionList()))
                .isEqualTo("first\n\nsecond");
    }

    @Test
    void headingAndSectionRendersBoth() {
        assertThat(mapper.writeValueAsString(new WithHeadingAndSection()))
                .isEqualTo("## Details\n\ncontent here");
    }

    @Test
    void unannotatedPojoFieldIsSkipped() {
        assertThat(mapper.writeValueAsString(new UnannotatedPojoIsSkipped()))
                .isEqualTo("visible");
    }
}
