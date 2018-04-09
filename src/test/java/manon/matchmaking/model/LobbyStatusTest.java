package manon.matchmaking.model;

import manon.matchmaking.document.LobbySolo;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class LobbyStatusTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(LobbyStatus.builder().build().toString()).contains(
                "lobbySolo");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        LobbyStatus filled = LobbyStatus.builder()
                .lobbySolo(LobbySolo.builder().id("1").build())
                .build();
        return new Object[][]{
                {LobbyStatus.builder().build(), LobbyStatus.builder().build(), true},
                {filled.toBuilder().build(), filled, true},
                {filled.toBuilder().lobbySolo(LobbySolo.builder().id("99").build()).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(LobbyStatus o1, LobbyStatus o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(LobbyStatus o1, LobbyStatus o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
