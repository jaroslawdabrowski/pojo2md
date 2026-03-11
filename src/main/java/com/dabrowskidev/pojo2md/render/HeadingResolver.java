package com.dabrowskidev.pojo2md.render;

import com.dabrowskidev.pojo2md.exception.MappingException;

import java.lang.reflect.Field;

public class HeadingResolver {

    String resolve(Field field, Object fieldValue) {
        if (field.getType() != String.class) {
            throw new MappingException("@Heading field '" + field.getName() + "' must be of type String");
        }
        return (String) fieldValue;
    }
}
