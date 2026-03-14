package io.github.jaroslawdabrowski.pojo2md;

import io.github.jaroslawdabrowski.pojo2md.annotation.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownMapperNestedTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class AgendaItem {
        @Paragraph
        String title;
        @UnorderedList
        List<String> participants;

        AgendaItem(String title, List<String> participants) {
            this.title = title;
            this.participants = participants;
        }
    }

    static class Meeting {
        @Heading(level = 2, value = "Agenda")
        List<AgendaItem> items = List.of(
                new AgendaItem("Sprint Update", List.of("Alice", "Bob")),
                new AgendaItem("Blockers", List.of("Carol"))
        );
    }

    static class SingleNested {
        @Heading(level = 1, value = "Doc")
        String title = null;
        @Section
        AgendaItem item = new AgendaItem("Details", List.of("Dave"));
    }

    static class NoAnnotationsInner {
        String plain = "text";
    }

    static class WithUnannotatedField {
        @Paragraph
        String para = "hello";
        NoAnnotationsInner inner = new NoAnnotationsInner();
    }

    @Test
    void listOfPojoWithHeading() {
        String result = mapper.writeValueAsString(new Meeting());
        assertThat(result).isEqualTo("""
                ## Agenda

                Sprint Update

                - Alice
                - Bob

                Blockers

                - Carol""");
    }

    @Test
    void singleNestedPojoWithSection() {
        String result = mapper.writeValueAsString(new SingleNested());
        assertThat(result).isEqualTo("""
                # Doc

                Details

                - Dave""");
    }

    @Test
    void unannotatedFieldIsSkipped() {
        String result = mapper.writeValueAsString(new WithUnannotatedField());
        assertThat(result).isEqualTo("hello");
    }
}
