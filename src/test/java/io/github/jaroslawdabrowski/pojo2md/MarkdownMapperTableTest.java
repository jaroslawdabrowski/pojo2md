package io.github.jaroslawdabrowski.pojo2md;

import io.github.jaroslawdabrowski.pojo2md.annotation.*;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;
import io.github.jaroslawdabrowski.pojo2md.exception.MappingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarkdownMapperTableTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    // ─── ROW CLASSES ─────────────────────────────────────────────────────────

    static class Employee {
        @Column(header = "Name")
        String name;
        @Column(header = "Role")
        String role;
        @Column(header = "Since")
        String since;

        Employee(String name, String role, String since) {
            this.name = name;
            this.role = role;
            this.since = since;
        }
    }

    static class AlignedRow {
        @Column(header = "Left", alignment = Align.LEFT)
        String left;
        @Column(header = "Center", alignment = Align.CENTER)
        String center;
        @Column(header = "Right", alignment = Align.RIGHT)
        String right;
        @Column(header = "Default")
        String def;

        AlignedRow(String left, String center, String right, String def) {
            this.left = left;
            this.center = center;
            this.right = right;
            this.def = def;
        }
    }

    static class MarkdownRow {
        @Column(header = "Formatted")
        Markdown formatted;

        MarkdownRow(Markdown formatted) {
            this.formatted = formatted;
        }
    }

    static class NullableCellRow {
        @Column(header = "A")
        String a;
        @Column(header = "B")
        String b;

        NullableCellRow(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    static class PipeCellRow {
        @Column(header = "Formula")
        String formula;

        PipeCellRow(String formula) {
            this.formula = formula;
        }
    }

    // ─── POJO CONTAINERS ─────────────────────────────────────────────────────

    static class EmployeeReport {
        @Table
        List<Employee> employees;

        EmployeeReport(List<Employee> employees) {
            this.employees = employees;
        }
    }

    static class HeadingTablePojo {
        @Heading(value = "Team")
        @Table
        List<Employee> employees;

        HeadingTablePojo(List<Employee> employees) {
            this.employees = employees;
        }
    }

    static class AlignedReport {
        @Table
        List<AlignedRow> rows;

        AlignedReport(List<AlignedRow> rows) {
            this.rows = rows;
        }
    }

    static class MarkdownReport {
        @Table
        List<MarkdownRow> rows;

        MarkdownReport(List<MarkdownRow> rows) {
            this.rows = rows;
        }
    }

    static class NullableCellReport {
        @Table
        List<NullableCellRow> rows;

        NullableCellReport(List<NullableCellRow> rows) {
            this.rows = rows;
        }
    }

    static class PipeReport {
        @Table
        List<PipeCellRow> rows;

        PipeReport(List<PipeCellRow> rows) {
            this.rows = rows;
        }
    }

    static class NullTablePojo {
        @Table
        List<Employee> employees = null;
    }

    static class EmptyTablePojo {
        @Table
        List<Employee> employees = List.of();
    }

    static class NotAListPojo {
        @Table
        String employees = "oops";
    }

    // ─── TESTS ────────────────────────────────────────────────────────────────

    @Test
    void basicTable() {
        var pojo = new EmployeeReport(List.of(
                new Employee("Alice", "Developer", "2020"),
                new Employee("Bob", "Designer", "2022")
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                | Name | Role | Since |
                | --- | --- | --- |
                | Alice | Developer | 2020 |
                | Bob | Designer | 2022 |""");
    }

    @Test
    void headingCombinedWithTable() {
        var pojo = new HeadingTablePojo(List.of(
                new Employee("Alice", "Developer", "2020")
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                # Team

                | Name | Role | Since |
                | --- | --- | --- |
                | Alice | Developer | 2020 |""");
    }

    @Test
    void alignmentVariants() {
        var pojo = new AlignedReport(List.of(
                new AlignedRow("a", "b", "c", "d")
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                | Left | Center | Right | Default |
                | :--- | :---: | ---: | --- |
                | a | b | c | d |""");
    }

    @Test
    void markdownCellValues() {
        var pojo = new MarkdownReport(List.of(
                new MarkdownRow(Markdown.of().b("bold")),
                new MarkdownRow(Markdown.of().i("italic"))
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                | Formatted |
                | --- |
                | **bold** |
                | *italic* |""");
    }

    @Test
    void nullCellRendersAsEmpty() {
        var pojo = new NullableCellReport(List.of(
                new NullableCellRow(null, "present"),
                new NullableCellRow("filled", null)
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                | A | B |
                | --- | --- |
                |  | present |
                | filled |  |""");
    }

    @Test
    void pipeInCellValueIsEscaped() {
        var pojo = new PipeReport(List.of(
                new PipeCellRow("A|B")
        ));

        assertThat(mapper.writeValueAsString(pojo)).isEqualTo("""
                | Formula |
                | --- |
                | A\\|B |""");
    }

    @Test
    void nullTableFieldThrowsMappingException() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NullTablePojo()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("null");
    }

    @Test
    void emptyTableRendersHeaderAndSeparatorOnly() {
        assertThat(mapper.writeValueAsString(new EmptyTablePojo())).isEqualTo("""
                | Name | Role | Since |
                | --- | --- | --- |""");
    }

    @Test
    void nonListFieldWithTableThrowsMappingException() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NotAListPojo()))
                .isInstanceOf(MappingException.class)
                .hasMessageContaining("List");
    }
}
