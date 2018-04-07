package manon.user.document;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class UserTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(User.builder().build().toString()).contains(
                "id", "username",
                "roles", "registrationState",
                "nickname", "email",
                "friendshipRequestsTo", "friendshipRequestsFrom",
                "friends", "friendshipEvents",
                "version", "creationDate", "updateDate");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {User.builder().build(), User.builder().build(), true},
                {User.builder().version(1).creationDate(Tools.now()).updateDate(Tools.now()).build(), User.builder().build(), true},
                {User.builder().version(1).build(), User.builder().build(), true},
                {User.builder().creationDate(Tools.now()).build(), User.builder().build(), true},
                {User.builder().updateDate(Tools.now()).build(), User.builder().build(), true},
                {User.builder().id("1").build(), User.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(User o1, User o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(User o1, User o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
