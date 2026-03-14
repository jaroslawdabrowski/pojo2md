package io.github.jaroslawdabrowski.pojo2md;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.jaroslawdabrowski.pojo2md.annotation.BlockQuote;
import io.github.jaroslawdabrowski.pojo2md.annotation.Heading;
import io.github.jaroslawdabrowski.pojo2md.annotation.OrderedList;
import io.github.jaroslawdabrowski.pojo2md.annotation.Paragraph;
import io.github.jaroslawdabrowski.pojo2md.annotation.UnorderedList;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;

class Pojo2MDComponentTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class AgendaItem {
        @Paragraph
        Markdown title;

        @Heading(level = 4, value = "Participants")
        @UnorderedList
        List<String> participants;

        AgendaItem(String titleText, List<String> participants) {
            this.title = Markdown.of().b(titleText);
            this.participants = participants;
        }
    }

    static class Meeting {
        @Heading(level = 1, value = "Q1 Planning Meeting")
        @Paragraph
        Markdown meta = Markdown.of()
                .b("Date:").t(" 2026-03-10").newLine()
                .b("Location:").t(" Conference Room A").newLine()
                .b("Duration:").t(" 60 minutes");

        @Heading(level = 2, value = "Participants")
        @UnorderedList
        List<String> participants = List.of(
                "Alice Smith — Product Manager",
                "Bob Jones — Engineering Lead",
                "Carol White — Design",
                "Dave Brown — QA"
        );

        @Heading(level = 2, value = "Agenda")
        List<AgendaItem> agendaItems = List.of(
                new AgendaItem("Status Update", List.of("Alice Smith", "Bob Jones")),
                new AgendaItem("Blockers", List.of("Carol White")),
                new AgendaItem("Q2 Roadmap", List.of("Alice Smith")),
                new AgendaItem("Design Review", List.of("Carol White", "Dave Brown"))
        );

        @Heading(level = 2, value = "Meeting Notes")
        @Paragraph
        Markdown statusUpdate = Markdown.of()
                .b("Sprint Progress:").t(" 80% of planned tasks completed.").newLine()
                .t("Key deliverables are on track for the March 15th deadline.");

        @Heading(level = 3, value = "Action Items")
        @OrderedList
        List<String> actionItems = List.of(
                "Bob to finalize API documentation by March 12th",
                "Carol to share updated mockups in Slack",
                "Alice to schedule follow-up with stakeholders"
        );

        @BlockQuote(level = 1)
        String nextMeeting = "Next meeting scheduled for March 17, 2026 at 10:00 AM";
    }

    @Test
    void meetingConvertsToMarkdown() {
        assertThat(mapper.writeValueAsString(new Meeting()))
                .isEqualTo("""
                        # Q1 Planning Meeting

                        **Date:** 2026-03-10\s\s
                        **Location:** Conference Room A\s\s
                        **Duration:** 60 minutes

                        ## Participants

                        - Alice Smith — Product Manager
                        - Bob Jones — Engineering Lead
                        - Carol White — Design
                        - Dave Brown — QA

                        ## Agenda

                        **Status Update**

                        #### Participants

                        - Alice Smith
                        - Bob Jones

                        **Blockers**

                        #### Participants

                        - Carol White

                        **Q2 Roadmap**

                        #### Participants

                        - Alice Smith

                        **Design Review**

                        #### Participants

                        - Carol White
                        - Dave Brown

                        ## Meeting Notes

                        **Sprint Progress:** 80% of planned tasks completed.\s\s
                        Key deliverables are on track for the March 15th deadline.

                        ### Action Items

                        1. Bob to finalize API documentation by March 12th
                        2. Carol to share updated mockups in Slack
                        3. Alice to schedule follow-up with stakeholders

                        > Next meeting scheduled for March 17, 2026 at 10:00 AM""");
    }
}
