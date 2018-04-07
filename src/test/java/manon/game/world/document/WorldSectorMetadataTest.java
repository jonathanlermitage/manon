package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldSectorMetadataTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldSectorMetadata.builder().build().toString()).contains(
                "id",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {WorldSectorMetadata.builder().build(), WorldSectorMetadata.builder().build(), true},
                {WorldSectorMetadata.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), WorldSectorMetadata.builder().build(), true},
                {WorldSectorMetadata.builder().version(1).build(), WorldSectorMetadata.builder().build(), true},
                {WorldSectorMetadata.builder().creationDate(Tools.now()).build(), WorldSectorMetadata.builder().build(), true},
                {WorldSectorMetadata.builder().updateDate(Tools.now()).build(), WorldSectorMetadata.builder().build(), true},
                {WorldSectorMetadata.builder().id("1").build(), WorldSectorMetadata.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldSectorMetadata o1, WorldSectorMetadata o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldSectorMetadata o1, WorldSectorMetadata o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
