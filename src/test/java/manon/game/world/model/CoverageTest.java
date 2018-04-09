package manon.game.world.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class CoverageTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(Coverage.builder().build().toString()).contains(
                "topRightX", "topRightY",
                "bottomLeftX", "bottomLeftY");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        Coverage filled = Coverage.builder()
                .topRightX(1)
                .topRightY(2)
                .bottomLeftX(3)
                .bottomLeftY(4)
                .build();
        return new Object[][]{
                {Coverage.builder().build(), Coverage.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().topRightX(99).build(), filled, false},
                {filled.toBuilder().topRightY(99).build(), filled, false},
                {filled.toBuilder().bottomLeftX(99).build(), filled, false},
                {filled.toBuilder().bottomLeftY(99).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(Coverage o1, Coverage o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Coverage o1, Coverage o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
