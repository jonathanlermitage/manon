package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldSectorTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldSector.builder().build().toString()).contains(
                "id", "name",
                "metadata", "coverage", "worldId",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {WorldSector.builder().build(), WorldSector.builder().build(), true},
                {WorldSector.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), WorldSector.builder().build(), true},
                {WorldSector.builder().version(1).build(), WorldSector.builder().build(), true},
                {WorldSector.builder().creationDate(Tools.now()).build(), WorldSector.builder().build(), true},
                {WorldSector.builder().updateDate(Tools.now()).build(), WorldSector.builder().build(), true},
                {WorldSector.builder().id("1").build(), WorldSector.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldSector o1, WorldSector o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldSector o1, WorldSector o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
