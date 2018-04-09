package manon.game.world.document;

import manon.game.world.model.Point;
import manon.game.world.model.WorldPointDescription;
import manon.game.world.model.WorldPointType;
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
        WorldPoint filled = WorldPoint.builder()
                .id("1")
                .name("n")
                .point(Point.builder().x(2).build())
                .description(WorldPointDescription.builder().type(WorldPointType.EVENT).build())
                .metadata(WorldPointMetadata.builder().id("2").build())
                .worldId("3")
                .sectorId("4")
                .version(5)
                .creationDate(Tools.now())
                .updateDate(Tools.now())
                .build();
        return new Object[][]{
                {WorldPoint.builder().build(), WorldPoint.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().id("99").build(), filled, false},
                {filled.toBuilder().name("updated").build(), filled, false},
                {filled.toBuilder().point(Point.builder().x(99).build()).build(), filled, false},
                {filled.toBuilder().description(WorldPointDescription.builder().type(WorldPointType.EMPTY).build()).build(), filled, false},
                {filled.toBuilder().metadata(WorldPointMetadata.builder().id("99").build()).build(), filled, false},
                {filled.toBuilder().worldId("99").build(), filled, false},
                {filled.toBuilder().sectorId("99").build(), filled, false},
                {filled.toBuilder().version(99).build(), filled, true},
                {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
                {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
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
