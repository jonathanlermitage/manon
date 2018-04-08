package manon.user.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class UsersStatsTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UsersStats.builder().build().toString()).contains(
                "id", "nbUsers",
                "creationDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {UsersStats.builder().build(), UsersStats.builder().build(), true},
                {UsersStats.builder().creationDate(Tools.now()).build(), UsersStats.builder().build(), true},
                {UsersStats.builder().creationDate(Tools.now()).build(), UsersStats.builder().creationDate(Tools.yesterday()).build(), true},
                {UsersStats.builder().creationDate(Tools.now()).build(), UsersStats.builder().creationDate(Tools.now()).build(), true},
                {UsersStats.builder().id("1").build(), UsersStats.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UsersStats o1, UsersStats o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UsersStats o1, UsersStats o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
