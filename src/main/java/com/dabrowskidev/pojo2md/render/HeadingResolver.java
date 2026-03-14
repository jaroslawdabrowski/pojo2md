package com.dabrowskidev.pojo2md.render;

import com.dabrowskidev.pojo2md.annotation.Heading;

public class HeadingResolver {

    String resolve(Heading heading) {
        return heading.value();
    }
}
