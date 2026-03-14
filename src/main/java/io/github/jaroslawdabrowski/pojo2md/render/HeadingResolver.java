package io.github.jaroslawdabrowski.pojo2md.render;

import io.github.jaroslawdabrowski.pojo2md.annotation.Heading;

public class HeadingResolver {

    String resolve(Heading heading) {
        return heading.value();
    }
}
