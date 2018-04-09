package manon.matchmaking.document;

import manon.matchmaking.model.LobbyLeague;
import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class LobbySoloTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(LobbySolo.builder().build().toString()).contains(
                "id", "userId", "skill", "league",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        LobbySolo filled = LobbySolo.builder()
                .id("1")
                .userId("2")
                .skill(3)
                .league(LobbyLeague.REGULAR)
                .version(4)
                .creationDate(Tools.now())
                .updateDate(Tools.now())
                .build();
        return new Object[][]{
                {LobbySolo.builder().build(), LobbySolo.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().id("99").build(), filled, false},
                {filled.toBuilder().userId("99").build(), filled, false},
                {filled.toBuilder().league(LobbyLeague.COMPETITIVE).build(), filled, false},
                {filled.toBuilder().version(99).build(), filled, true},
                {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
                {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(LobbySolo o1, LobbySolo o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(LobbySolo o1, LobbySolo o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
