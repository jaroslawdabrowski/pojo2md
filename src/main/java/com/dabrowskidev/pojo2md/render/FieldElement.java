package com.dabrowskidev.pojo2md.render;

import com.dabrowskidev.pojo2md.annotation.Heading;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public record FieldElement(Field field, Heading heading, Annotation contentAnnotation, int declarationIndex) {
    public boolean isNested() { return heading == null && contentAnnotation == null; }
}
