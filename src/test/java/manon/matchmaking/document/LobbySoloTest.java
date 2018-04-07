package manon.matchmaking.document;

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
        return new Object[][]{
                {LobbySolo.builder().build(), LobbySolo.builder().build(), true},
                {LobbySolo.builder().creationDate(Tools.now()).updateDate(Tools.now()).build(), LobbySolo.builder().build(), true},
                {LobbySolo.builder().version(1).build(), LobbySolo.builder().build(), true},
                {LobbySolo.builder().creationDate(Tools.now()).build(), LobbySolo.builder().build(), true},
                {LobbySolo.builder().updateDate(Tools.now()).build(), LobbySolo.builder().build(), true},
                {LobbySolo.builder().id("1").build(), LobbySolo.builder().build(), false}
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
