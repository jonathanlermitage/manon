package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldPointMetadataTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldPointMetadata.builder().build().toString()).contains(
                "id",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {WorldPointMetadata.builder().build(), WorldPointMetadata.builder().build(), true},
                {WorldPointMetadata.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), WorldPointMetadata.builder().build(), true},
                {WorldPointMetadata.builder().version(1).build(), WorldPointMetadata.builder().build(), true},
                {WorldPointMetadata.builder().creationDate(Tools.now()).build(), WorldPointMetadata.builder().build(), true},
                {WorldPointMetadata.builder().updateDate(Tools.now()).build(), WorldPointMetadata.builder().build(), true},
                {WorldPointMetadata.builder().id("1").build(), WorldPointMetadata.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldPointMetadata o1, WorldPointMetadata o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldPointMetadata o1, WorldPointMetadata o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
