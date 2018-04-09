package manon.game.world.form;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldRegistrationFormTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(WorldRegistrationForm.builder().build().toString()).contains(
                "name",
                "sectorWidth", "sectorHeight",
                "nbSectorsHorizontal", "nbSectorsVertical",
                "nbPointsPerSector");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        WorldRegistrationForm filled = WorldRegistrationForm.builder()
                .name("n")
                .sectorWidth(1)
                .sectorHeight(2)
                .nbSectorsHorizontal(3)
                .nbSectorsVertical(4)
                .nbPointsPerSector(5)
                .build();
        return new Object[][]{
                {WorldRegistrationForm.builder().build(), WorldRegistrationForm.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().name("updated").build(), filled, false},
                {filled.toBuilder().sectorWidth(99).build(), filled, false},
                {filled.toBuilder().sectorHeight(99).build(), filled, false},
                {filled.toBuilder().nbSectorsHorizontal(99).build(), filled, false},
                {filled.toBuilder().nbSectorsVertical(99).build(), filled, false},
                {filled.toBuilder().nbPointsPerSector(99).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(WorldRegistrationForm o1, WorldRegistrationForm o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(WorldRegistrationForm o1, WorldRegistrationForm o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
