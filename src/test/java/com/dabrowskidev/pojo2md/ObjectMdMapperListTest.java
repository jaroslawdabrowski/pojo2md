package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.OrderedList;
import com.dabrowskidev.pojo2md.annotation.UnorderedList;
import com.dabrowskidev.pojo2md.builder.Markdown;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectMdMapperListTest {

    private final ObjectMdMapper mapper = new ObjectMdMapper();

    static class OrderedStringsPojo {
        @OrderedList
        List<String> items = List.of("first", "second", "third");
    }

    static class UnorderedStringsPojo {
        @UnorderedList
        List<String> items = List.of("alpha", "beta");
    }

    static class OrderedMarkdownPojo {
        @OrderedList
        List<Markdown> items = List.of(
                Markdown.of().b("bold item"),
                Markdown.of().i("italic item")
        );
    }

    static class UnorderedMarkdownPojo {
        @UnorderedList
        List<Markdown> items = List.of(Markdown.of().t("plain"));
    }

    static class EmptyListPojo {
        @OrderedList
        List<String> items = List.of();
    }

    @Test
    void orderedListStrings() {
        assertThat(mapper.writeValueAsString(new OrderedStringsPojo()))
                .isEqualTo("""
                        1. first
                        2. second
                        3. third""");
    }

    @Test
    void unorderedListStrings() {
        assertThat(mapper.writeValueAsString(new UnorderedStringsPojo()))
                .isEqualTo("""
                        - alpha
                        - beta""");
    }

    @Test
    void orderedListMarkdownItems() {
        assertThat(mapper.writeValueAsString(new OrderedMarkdownPojo()))
                .isEqualTo("""
                        1. **bold item**
                        2. *italic item*""");
    }

    @Test
    void unorderedListMarkdownItems() {
        assertThat(mapper.writeValueAsString(new UnorderedMarkdownPojo()))
                .isEqualTo("- plain");
    }

    @Test
    void emptyListProducesEmptyString() {
        assertThat(mapper.writeValueAsString(new EmptyListPojo())).isEmpty();
    }
}
