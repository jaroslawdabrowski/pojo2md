package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.*;
import com.dabrowskidev.pojo2md.exception.MappingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectMdMapperRobustnessTest {

    private final ObjectMdMapper mapper = new ObjectMdMapper();

    // ─── NULL FIELD VALUES ────────────────────────────────────────────────────

    static class NullParagraph {
        @Paragraph
        String text = null;
    }

    static class NullBlockQuote {
        @BlockQuote
        String quote = null;
    }

    static class NullSection {
        @Section
        Object inner = null;
    }

    static class HeadingWithNullParagraph {
        @Heading(level = 2, value = "Section")
        @Paragraph
        String body = null;
    }

    @Test
    void nullParagraphIsSkipped() {
        assertThat(mapper.writeValueAsString(new NullParagraph())).isEmpty();
    }

    @Test
    void nullBlockQuoteIsSkipped() {
        assertThat(mapper.writeValueAsString(new NullBlockQuote())).isEmpty();
    }

    @Test
    void nullSectionIsSkipped() {
        assertThat(mapper.writeValueAsString(new NullSection())).isEmpty();
    }

    @Test
    void headingWithNullParagraphRendersJustHeading() {
        assertThat(mapper.writeValueAsString(new HeadingWithNullParagraph()))
                .isEqualTo("## Section");
    }

    // ─── NULL LIST ────────────────────────────────────────────────────────────

    static class NullUnorderedList {
        @UnorderedList
        List<String> items = null;
    }

    static class NullOrderedList {
        @OrderedList
        List<String> items = null;
    }

    @Test
    void nullUnorderedListThrowsDescriptiveError() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NullUnorderedList()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("null");
    }

    @Test
    void nullOrderedListThrowsDescriptiveError() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NullOrderedList()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("null");
    }

    // ─── NULL ITEMS IN LIST ────────────────────────────────────────────────────

    static class ListWithNullItems {
        @UnorderedList
        List<String> items = new ArrayList<>() {{
            add("first");
            add(null);
            add("third");
        }};
    }

    @Test
    void nullItemInListRendersAsEmptyEntry() {
        String result = mapper.writeValueAsString(new ListWithNullItems());
        assertThat(result).isEqualTo("- first\n- \n- third");
    }

    // ─── EMPTY VALUES ─────────────────────────────────────────────────────────

    static class EmptyParagraph {
        @Paragraph
        String text = "";
    }

    static class EmptyBlockQuote {
        @BlockQuote
        String quote = "";
    }

    static class EmptyList {
        @UnorderedList
        List<String> items = List.of();
    }

    @Test
    void emptyParagraphIsSkipped() {
        assertThat(mapper.writeValueAsString(new EmptyParagraph())).isEmpty();
    }

    @Test
    void emptyBlockQuoteIsSkipped() {
        assertThat(mapper.writeValueAsString(new EmptyBlockQuote())).isEmpty();
    }

    @Test
    void emptyListIsSkipped() {
        assertThat(mapper.writeValueAsString(new EmptyList())).isEmpty();
    }

    // ─── @SECTION ON WRONG TYPE ────────────────────────────────────────────────

    static class SectionOnString {
        @Section
        String text = "not a POJO";
    }

    @Test
    void sectionOnStringThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new SectionOnString()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("@Section")
                .hasMessageContaining("@Paragraph");
    }

    // ─── STATIC FIELDS ────────────────────────────────────────────────────────

    static class WithStaticAnnotatedField {
        @Paragraph
        static String STATIC = "should not appear";

        @Paragraph
        String instance = "should appear";
    }

    @Test
    void staticFieldIsSkipped() {
        String result = mapper.writeValueAsString(new WithStaticAnnotatedField());
        assertThat(result).isEqualTo("should appear");
        assertThat(result).doesNotContain("should not appear");
    }

    // ─── MIXED: HEADING + NULL LIST ───────────────────────────────────────────

    static class HeadingWithNullList {
        @Heading(level = 2, value = "Items")
        @UnorderedList
        List<String> items = null;
    }

    @Test
    void headingWithNullListThrowsDescriptiveError() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new HeadingWithNullList()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("null");
    }
}
