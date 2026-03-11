package com.dabrowskidev.pojo2md;

import com.dabrowskidev.pojo2md.annotation.Heading;
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

    static class NonStringHeadingPojo {
        @Heading(level = 1)
        Object obj = new Object();
    }

    @Test
    void stringFieldUsedAsHeadingText() {
        assertThat(mapper.writeValueAsString(new StringFieldPojo())).isEqualTo("## From Field");
    }

    @Test
    void nonStringFieldThrows() {
        assertThatThrownBy(() -> mapper.writeValueAsString(new NonStringHeadingPojo()))
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
