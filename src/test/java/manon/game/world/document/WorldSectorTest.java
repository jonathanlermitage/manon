package manon.game.world.document;

import manon.game.world.model.Coverage;
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
        WorldSector filled = WorldSector.builder()
                .id("1")
                .name("n")
                .metadata(WorldSectorMetadata.builder().id("2").build())
                .coverage(Coverage.builder().topRightX(3).build())
                .worldId("4")
                .version(5)
                .creationDate(Tools.now())
                .updateDate(Tools.now())
                .build();
        return new Object[][]{
                {WorldSector.builder().build(), WorldSector.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().id("99").build(), filled, false},
                {filled.toBuilder().name("updated").build(), filled, false},
                {filled.toBuilder().metadata(WorldSectorMetadata.builder().id("99").build()).build(), filled, false},
                {filled.toBuilder().coverage(Coverage.builder().topRightX(99).build()).build(), filled, false},
                {filled.toBuilder().worldId("99").build(), filled, false},
                {filled.toBuilder().version(99).build(), filled, true},
                {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
                {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
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
