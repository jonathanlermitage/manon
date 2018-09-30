package manon.app.stats.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class MethodExecutionStatsTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(sample().toString()).contains(
            "service", "calls", "minTime", "maxTime", "totalTime", "times");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
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
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(MethodExecutionStats o1, MethodExecutionStats o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(MethodExecutionStats o1, MethodExecutionStats o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
    
    private MethodExecutionStats sample() {
        return new MethodExecutionStats("s", 0, 0, 0, 0, singletonList(0L));
    }
}
