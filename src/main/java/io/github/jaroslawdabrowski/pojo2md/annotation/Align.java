package io.github.jaroslawdabrowski.pojo2md.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Align {
    NONE("---"),
    LEFT(":---"),
    CENTER(":---:"),
    RIGHT("---:");

    private final String separator;
}
