package manon.game.world.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
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
        World filled = World.builder()
                .id("1")
                .name("n")
                .nbSectors(2)
                .sectors(singletonList(WorldSector.builder().id("3").build()))
                .nbPoints(4)
                .points(singletonList(WorldPoint.builder().id("5").build()))
                .version(6)
                .creationDate(Tools.now())
                .updateDate(Tools.now())
                .build();
        return new Object[][]{
                {World.builder().build(), World.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().id("99").build(), filled, false},
                {filled.toBuilder().name("updated").build(), filled, false},
                {filled.toBuilder().nbSectors(99).build(), filled, false},
                {filled.toBuilder().sectors(singletonList(WorldSector.builder().id("99").build())).build(), filled, false},
                {filled.toBuilder().nbPoints(99).build(), filled, false},
                {filled.toBuilder().points(singletonList(WorldPoint.builder().id("99").build())).build(), filled, false},
                {filled.toBuilder().version(99).build(), filled, true},
                {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
                {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
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
