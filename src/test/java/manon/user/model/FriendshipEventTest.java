package manon.user.model;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static manon.user.model.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class FriendshipEventTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(FriendshipEvent.builder().build().toString()).contains(
                "date", "code", "params");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        return new Object[][]{
                {FriendshipEvent.builder().build(), FriendshipEvent.builder().build(), true},
                {FriendshipEvent.builder().date(Tools.now()).build(), FriendshipEvent.builder().build(), true},
                {FriendshipEvent.builder().date(Tools.now()).build(), FriendshipEvent.builder().date(Tools.yesterday()).build(), false},
                {FriendshipEvent.builder().code(TARGET_SENT_FRIEND_REQUEST).build(), FriendshipEvent.builder().build(), false},
                {FriendshipEvent.builder().params(Collections.emptyList()).build(), FriendshipEvent.builder().build(), false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertEquals(o1.equals(o2), expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertEquals(o1.hashCode() == o2.hashCode(), expectedEqual);
    }
}
