package com.dabrowskidev.pojo2md.render;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public record FieldElement(Field field, Annotation annotation, int declarationIndex) {
    public boolean isNested() { return annotation == null; }
}
