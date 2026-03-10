package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.Heading;
import com.dabrowskidev.pojo2md.annotation.HeadingValue;
import com.dabrowskidev.pojo2md.exception.MappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectMdMapperHeadingTest {

    private final ObjectMdMapper mapper = new ObjectMdMapper();

    static class StringFieldPojo {
        @Heading(level = 2)
        String title = "From Field";
    }

    static class NestedTitle {
        @HeadingValue
        String name;
        int score;

        NestedTitle(String name) { this.name = name; }
    }

    static class NestedHeadingPojo {
        @Heading(level = 3)
        NestedTitle nested = new NestedTitle("Nested Value");
    }

    static class NoHeadingValuePojo {
        @Heading(level = 1)
        Object obj = new Object();
    }

    static class MultiHeadingValuePojo {
        @HeadingValue
        String a = "a";
        @HeadingValue
        String b = "b";
    }

    static class MultiHeadingValueHostPojo {
        @Heading(level = 1)
        MultiHeadingValuePojo nested = new MultiHeadingValuePojo();
    }

    @Test
    void stringFieldUsedAsHeadingText() {
        assertThat(mapper.writeValueAsString(new StringFieldPojo())).isEqualTo("## From Field");
    }

    @Test
    void nestedClassWithHeadingValue() {
        assertThat(mapper.writeValueAsString(new NestedHeadingPojo())).isEqualTo("### Nested Value");
    }

    @Test
    void noHeadingValueThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NoHeadingValuePojo()))
                .isInstanceOf(MappingException.class);
    }

    @Test
    void multipleHeadingValuesThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new MultiHeadingValueHostPojo()))
                .isInstanceOf(MappingException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void allHeadingLevels(int level) {
        class LevelPojo {
            @Heading(level = 1)
            String title = "T";
        }
        assertThat(mapper.writeValueAsString(new LevelPojo())).startsWith("#");
    }
}
