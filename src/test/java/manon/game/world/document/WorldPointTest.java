package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldPointTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldPoint.builder().build().toString()).contains(
                "id", "name",
                "point", "description",
                "metadata", "worldId", "sectorId",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {WorldPoint.builder().build(), WorldPoint.builder().build(), true},
                {WorldPoint.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), WorldPoint.builder().build(), true},
                {WorldPoint.builder().version(1).build(), WorldPoint.builder().build(), true},
                {WorldPoint.builder().creationDate(Tools.now()).build(), WorldPoint.builder().build(), true},
                {WorldPoint.builder().updateDate(Tools.now()).build(), WorldPoint.builder().build(), true},
                {WorldPoint.builder().id("1").build(), WorldPoint.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldPoint o1, WorldPoint o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldPoint o1, WorldPoint o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
