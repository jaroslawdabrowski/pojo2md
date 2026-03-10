package com.dabrowskidev.pojo2md.render;

import com.dabrowskidev.pojo2md.annotation.HeadingValue;
import com.dabrowskidev.pojo2md.exception.MappingException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class HeadingResolver {

    String resolve(Field field, Object fieldValue) {
        if (field.getType() == String.class) {
            return (String) fieldValue;
        }
        return resolveFromHeadingValue(fieldValue);
    }

    private String resolveFromHeadingValue(Object obj) {
        if (obj == null) {
            throw new MappingException("Cannot resolve @HeadingValue from null object");
        }
        List<Field> headingValueFields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(HeadingValue.class))
                .toList();

        if (headingValueFields.isEmpty()) {
            throw new MappingException(
                    "No @HeadingValue field found in " + obj.getClass().getSimpleName());
        }
        if (headingValueFields.size() > 1) {
            throw new MappingException(
                    "Multiple @HeadingValue fields found in " + obj.getClass().getSimpleName()
                    + ", exactly one is required");
        }

        Field hvField = headingValueFields.getFirst();
        if (hvField.getType() != String.class) {
            throw new MappingException(
                    "@HeadingValue field '" + hvField.getName() + "' must be of type String");
        }
        hvField.setAccessible(true);
        try {
            return (String) hvField.get(obj);
        } catch (IllegalAccessException e) {
            throw new MappingException("Cannot access @HeadingValue field: " + hvField.getName(), e);
        }
    }
}
