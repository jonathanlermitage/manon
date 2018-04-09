package manon.game.world.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldPointDescriptionTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldPointDescription.builder().build().toString()).contains(
                "type");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        WorldPointDescription filled = WorldPointDescription.builder()
                .type(WorldPointType.EVENT)
                .build();
        return new Object[][]{
                {WorldPointDescription.builder().build(), WorldPointDescription.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().type(WorldPointType.EMPTY).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldPointDescription o1, WorldPointDescription o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldPointDescription o1, WorldPointDescription o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
