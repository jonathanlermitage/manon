package manon.model.app;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.singletonList;

public class MethodExecutionStatsTest {

    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(sample().toString()).contains(
            "service", "calls", "minTime", "maxTime", "totalTime", "times");
    }

    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        MethodExecutionStats sample = sample();
        MethodExecutionStats newService = sample();
        newService.setService("updated");
        MethodExecutionStats newCalls = sample();
        newCalls.setCalls(99);
        MethodExecutionStats newMinTime = sample();
        newMinTime.setMinTime(99);
        MethodExecutionStats newMaxTime = sample();
        newMaxTime.setMaxTime(99);
        MethodExecutionStats newTotalTime = sample();
        newTotalTime.setTotalTime(99);
        MethodExecutionStats newTimes = sample();
        newTimes.setTimes(singletonList(99L));
        return new Object[][]{
            {sample(), sample(), true},
            {newService, sample, false},
            {newCalls, sample, false},
            {newMinTime, sample, false},
            {newMaxTime, sample, false},
            {newTotalTime, sample, false},
            {newTimes, sample, false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(MethodExecutionStats o1, MethodExecutionStats o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(MethodExecutionStats o1, MethodExecutionStats o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    private static MethodExecutionStats sample() {
        return new MethodExecutionStats("s", 0, 0, 0, 0, singletonList(0L));
    }
}
