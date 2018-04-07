package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(World.builder().build().toString()).contains(
                "id", "name",
                "nbSectors", "sectors",
                "nbPoints", "points",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {World.builder().build(), World.builder().build(), true},
                {World.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), World.builder().build(), true},
                {World.builder().version(1).build(), World.builder().build(), true},
                {World.builder().creationDate(Tools.now()).build(), World.builder().build(), true},
                {World.builder().updateDate(Tools.now()).build(), World.builder().build(), true},
                {World.builder().id("1").build(), World.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(World o1, World o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(World o1, World o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
