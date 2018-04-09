package manon.game.world.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class PointTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(Point.builder().build().toString()).contains(
                "x", "y");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        Point filled = Point.builder()
                .x(1)
                .y(2)
                .build();
        return new Object[][]{
                {Point.builder().build(), Point.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().x(99).build(), filled, false},
                {filled.toBuilder().y(99).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(Point o1, Point o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Point o1, Point o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
