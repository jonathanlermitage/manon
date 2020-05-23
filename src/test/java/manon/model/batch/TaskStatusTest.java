package manon.model.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStatusTest {

    @Test
    void shouldVerifyToString() {
        assertThat(TaskStatus.builder().build().toString()).contains(
            "running", "exitCode", "exitDescription");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        TaskStatus filled = TaskStatus.builder()
            .running(true)
            .exitCode("c")
            .exitDescription("d")
            .build();
        return new Object[][]{
            {TaskStatus.builder().build(), TaskStatus.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().running(false).build(), filled, false},
            {filled.toBuilder().exitCode("updated").build(), filled, false},
            {filled.toBuilder().exitDescription("updated").build(), filled, false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(TaskStatus o1, TaskStatus o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(TaskStatus o1, TaskStatus o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
