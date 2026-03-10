package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectMdMapperOrderingTest {

    private final ObjectMdMapper mapper = new ObjectMdMapper();

    static class MixedPojo {
        @Heading(level = 1)
        String title = "Title";
        @BlockQuote(level = 1)
        String quote = "a quote";
        @Paragraph
        String last = "last paragraph";
        @OrderedList
        List<String> items = List.of("item");
    }

    static class TwoParagraphsPojo {
        @Paragraph
        String first = "first";
        @Paragraph
        String second = "second";
    }

    @Test
    void mixedAnnotationsRenderInDeclarationOrder() {
        String result = mapper.writeValueAsString(new MixedPojo());
        assertThat(result.indexOf("# Title")).isLessThan(result.indexOf("> a quote"));
        assertThat(result.indexOf("> a quote")).isLessThan(result.indexOf("last paragraph"));
        assertThat(result.indexOf("last paragraph")).isLessThan(result.indexOf("1. item"));
    }

    @Test
    void declarationOrderRespected() {
        String result = mapper.writeValueAsString(new TwoParagraphsPojo());
        assertThat(result.indexOf("first")).isLessThan(result.indexOf("second"));
    }
}
