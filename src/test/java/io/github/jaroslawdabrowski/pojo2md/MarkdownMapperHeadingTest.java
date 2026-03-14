package io.github.jaroslawdabrowski.pojo2md;

import io.github.jaroslawdabrowski.pojo2md.annotation.Heading;
import io.github.jaroslawdabrowski.pojo2md.annotation.OrderedList;
import io.github.jaroslawdabrowski.pojo2md.annotation.Paragraph;
import io.github.jaroslawdabrowski.pojo2md.annotation.UnorderedList;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;
import io.github.jaroslawdabrowski.pojo2md.exception.MappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarkdownMapperHeadingTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class HeadingOnlyPojo {
        @Heading(level = 2, value = "Section Title")
        String dummy = null;
    }

    static class HeadingWithStringContentPojo {
        @Heading(level = 2, value = "Section")
        String body = "Some content here";
    }

    static class HeadingWithMarkdownContentPojo {
        @Heading(level = 1, value = "Title")
        Markdown body = Markdown.of().b("bold").t(" text");
    }

    static class HeadingWithParagraphPojo {
        @Heading(level = 2, value = "Notes")
        @Paragraph
        String content = "Paragraph content";
    }

    static class HeadingWithUnorderedListPojo {
        @Heading(level = 2, value = "Items")
        @UnorderedList
        List<String> items = List.of("Alpha", "Beta", "Gamma");
    }

    static class HeadingWithOrderedListPojo {
        @Heading(level = 3, value = "Steps")
        @OrderedList
        List<String> steps = List.of("First", "Second", "Third");
    }

    static class MultipleContentAnnotationsPojo {
        @Paragraph
        @UnorderedList
        List<String> bad = List.of("x");
    }

    @Test
    void headingOnlyRendersJustHeadingWhenFieldIsNull() {
        assertThat(mapper.writeValueAsString(new HeadingOnlyPojo())).isEqualTo("## Section Title");
    }

    @Test
    void headingOnStringFieldRendersHeadingThenContent() {
        assertThat(mapper.writeValueAsString(new HeadingWithStringContentPojo()))
                .isEqualTo("## Section\n\nSome content here");
    }

    @Test
    void headingOnMarkdownFieldRendersHeadingThenContent() {
        assertThat(mapper.writeValueAsString(new HeadingWithMarkdownContentPojo()))
                .isEqualTo("# Title\n\n**bold** text");
    }

    @Test
    void headingCombinedWithParagraphAnnotation() {
        assertThat(mapper.writeValueAsString(new HeadingWithParagraphPojo()))
                .isEqualTo("## Notes\n\nParagraph content");
    }

    @Test
    void headingCombinedWithUnorderedList() {
        assertThat(mapper.writeValueAsString(new HeadingWithUnorderedListPojo()))
                .isEqualTo("## Items\n\n- Alpha\n- Beta\n- Gamma");
    }

    @Test
    void headingCombinedWithOrderedList() {
        assertThat(mapper.writeValueAsString(new HeadingWithOrderedListPojo()))
                .isEqualTo("### Steps\n\n1. First\n2. Second\n3. Third");
    }

    @Test
    void multipleContentAnnotationsThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new MultipleContentAnnotationsPojo()))
                .isInstanceOf(MappingException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void allHeadingLevels(int level) {
        class LevelPojo {
            @Heading(level = 1, value = "T")
            String dummy = null;
        }
        assertThat(mapper.writeValueAsString(new LevelPojo())).startsWith("#");
    }
}
