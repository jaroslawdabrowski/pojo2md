package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.*;
import com.dabrowskidev.pojo2md.exception.MappingException;
import com.dabrowskidev.pojo2md.render.ElementRenderer;
import com.dabrowskidev.pojo2md.render.FieldElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectMdMapper {

    private final ElementRenderer renderer = new ElementRenderer(this::writeValueAsString);

    public String writeValueAsString(Object pojo) {
        Field[] declaredFields = pojo.getClass().getDeclaredFields();
        List<FieldElement> elements = new ArrayList<>();

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            Annotation recognized = findRecognizedAnnotation(field);
            if (recognized != null) {
                elements.add(new FieldElement(field, recognized, i));
            } else if (hasRecognizedAnnotations(effectiveType(field))) {
                elements.add(new FieldElement(field, null, i));
            }
        }

        elements.sort(Comparator.comparingInt(FieldElement::declarationIndex));

        return elements.stream()
                .map(el -> {
                    el.field().setAccessible(true);
                    try {
                        Object value = el.field().get(pojo);
                        return renderer.render(el, value);
                    } catch (IllegalAccessException e) {
                        throw new MappingException(
                                "Cannot access field: " + el.field().getName(), e);
                    }
                })
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("\n\n"))
                .stripTrailing();
    }

    private Annotation findRecognizedAnnotation(Field field) {
        List<Annotation> found = new ArrayList<>();
        for (Annotation a : field.getAnnotations()) {
            if (isRecognizedAnnotation(a)) found.add(a);
        }
        if (found.size() > 1) {
            throw new MappingException(
                    "Field '" + field.getName() + "' has multiple mapping annotations; only one is allowed");
        }
        return found.isEmpty() ? null : found.getFirst();
    }

    private boolean isRecognizedAnnotation(Annotation a) {
        return a instanceof Heading
                || a instanceof Paragraph
                || a instanceof BlockQuote
                || a instanceof OrderedList
                || a instanceof UnorderedList;
    }

    private Class<?> effectiveType(Field field) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType pt && pt.getRawType() == List.class) {
            Type arg = pt.getActualTypeArguments()[0];
            if (arg instanceof Class<?> cls) return cls;
        }
        return field.getType();
    }

    private boolean hasRecognizedAnnotations(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .flatMap(f -> Arrays.stream(f.getAnnotations()))
                .anyMatch(this::isRecognizedAnnotation);
    }
}
