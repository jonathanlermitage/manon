package manon.user.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class UserSnapshotTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserSnapshot.builder().build().toString()).contains(
                "id", "user",
                "creationDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {UserSnapshot.builder().build(), UserSnapshot.builder().build(), true},
                {UserSnapshot.builder().creationDate(Tools.now()).build(), UserSnapshot.builder().build(), true},
                {UserSnapshot.builder().creationDate(Tools.now()).build(), UserSnapshot.builder().creationDate(Tools.yesterday()).build(), true},
                {UserSnapshot.builder().creationDate(Tools.now()).build(), UserSnapshot.builder().creationDate(Tools.now()).build(), true},
                {UserSnapshot.builder().id("1").build(), UserSnapshot.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserSnapshot o1, UserSnapshot o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserSnapshot o1, UserSnapshot o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
