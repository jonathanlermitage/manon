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
        return new Object[][]{
                {Coverage.builder().build(), Coverage.builder().build(), true},
                {Coverage.builder().topRightX(1).build(), Coverage.builder().build(), false},
                {Coverage.builder().topRightY(1).build(), Coverage.builder().build(), false},
                {Coverage.builder().bottomLeftX(1).build(), Coverage.builder().build(), false},
                {Coverage.builder().bottomLeftY(1).build(), Coverage.builder().build(), false}
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
