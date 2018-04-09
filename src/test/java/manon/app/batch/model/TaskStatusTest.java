package manon.app.batch.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class TaskStatusTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(TaskStatus.builder().build().toString()).contains(
                "running", "exitCode", "exitDescription");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
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
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(TaskStatus o1, TaskStatus o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(TaskStatus o1, TaskStatus o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
