package io.github.jaroslawdabrowski.pojo2md.render;

import io.github.jaroslawdabrowski.pojo2md.annotation.Column;
import io.github.jaroslawdabrowski.pojo2md.builder.Markdown;
import io.github.jaroslawdabrowski.pojo2md.exception.MappingException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableRenderer {

    public String render(Field tableField, Object value) {
        if (value == null) {
            throw new MappingException("Field '" + tableField.getName() + "' annotated with @Table is null");
        }
        if (!(value instanceof List<?> rows)) {
            throw new MappingException(
                    "Field '" + tableField.getName() + "' annotated with @Table must be of type List");
        }

        Class<?> rowClass = resolveRowClass(tableField);
        List<Field> columns = getColumnFields(rowClass);

        String header = buildRow(columns.stream().map(f -> f.getAnnotation(Column.class).header()).toList());
        String separator = buildRow(columns.stream()
                .map(f -> f.getAnnotation(Column.class).alignment().getSeparator())
                .toList());

        List<String> dataRows = rows.stream()
                .filter(Objects::nonNull)
                .map(row -> buildRow(columns.stream().map(col -> getCellValue(col, row)).toList()))
                .toList();

        if (dataRows.isEmpty()) return header + "\n" + separator;
        return header + "\n" + separator + "\n" + String.join("\n", dataRows);
    }

    private Class<?> resolveRowClass(Field tableField) {
        if (tableField.getGenericType() instanceof ParameterizedType pt
                && pt.getActualTypeArguments()[0] instanceof Class<?> rowClass) {
            return rowClass;
        }
        throw new MappingException(
                "Field '" + tableField.getName() + "' annotated with @Table must be a List with a concrete type parameter");
    }

    private List<Field> getColumnFields(Class<?> rowClass) {
        Field[] fields = rowClass.getDeclaredFields();
        List<Field> columns = Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(Column.class))
                .toList();
        if (columns.isEmpty()) {
            throw new MappingException("Row class '" + rowClass.getSimpleName() + "' has no @Column-annotated fields");
        }
        columns.forEach(f -> f.setAccessible(true));
        return columns;
    }

    private String getCellValue(Field col, Object row) {
        try {
            Object val = col.get(row);
            if (val == null) return "";
            String raw = switch (val) {
                case Markdown m -> m.render();
                case String s -> s;
                default -> val.toString();
            };
            return sanitize(raw);
        } catch (IllegalAccessException e) {
            throw new MappingException("Cannot access column field: " + col.getName(), e);
        }
    }

    private String sanitize(String value) {
        return value.replace("\n", " ").replace("|", "\\|");
    }

    private String buildRow(List<String> cells) {
        return "| " + String.join(" | ", cells) + " |";
    }
}
