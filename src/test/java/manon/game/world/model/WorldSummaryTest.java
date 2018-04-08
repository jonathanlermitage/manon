package manon.game.world.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldSummaryTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldSummary.builder().build().toString()).contains(
                "id", "name",
                "nbSectors", "nbPoints");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {WorldSummary.builder().build(), WorldSummary.builder().build(), true},
                {WorldSummary.builder().id("1").build(), WorldSummary.builder().build(), false},
                {WorldSummary.builder().name("foo").build(), WorldSummary.builder().build(), false},
                {WorldSummary.builder().nbSectors(1).build(), WorldSummary.builder().build(), false},
                {WorldSummary.builder().nbPoints(1).build(), WorldSummary.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldSummary o1, WorldSummary o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldSummary o1, WorldSummary o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
