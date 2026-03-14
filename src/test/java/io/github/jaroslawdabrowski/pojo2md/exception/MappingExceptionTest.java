package io.github.jaroslawdabrowski.pojo2md.exception;

import io.github.jaroslawdabrowski.pojo2md.MarkdownMapper;
import io.github.jaroslawdabrowski.pojo2md.annotation.OrderedList;
import io.github.jaroslawdabrowski.pojo2md.annotation.Paragraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MappingExceptionTest {

    private final MarkdownMapper mapper = new MarkdownMapper();

    static class MultipleAnnotationsPojo {
        @Paragraph
        @OrderedList
        String field = "value";
    }

    static class WrongTypeForListPojo {
        @OrderedList
        String notAList = "oops";
    }

    @Test
    void multipleAnnotationsThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new MultipleAnnotationsPojo()))
                .isInstanceOf(MappingException.class);
    }

    @Test
    void wrongTypeForListThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new WrongTypeForListPojo()))
                .isInstanceOf(MappingException.class);
    }
}
