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
        WorldSummary filled = WorldSummary.builder()
                .id("1")
                .name("n")
                .nbSectors(2)
                .nbPoints(3)
                .build();
        return new Object[][]{
                {WorldSummary.builder().build(), WorldSummary.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().id("99").build(), filled, false},
                {filled.toBuilder().name("updated").build(), filled, false},
                {filled.toBuilder().nbPoints(99).build(), filled, false},
                {filled.toBuilder().nbPoints(99).build(), filled, false}
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
