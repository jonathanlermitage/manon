package manon.user.model;

import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
import static manon.user.model.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipEventTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(FriendshipEvent.builder().build().toString()).contains(
            "date", "code", "params");
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipEvent filled = FriendshipEvent.builder()
            .date(Tools.now())
            .code(TARGET_SENT_FRIEND_REQUEST)
            .params(singletonList("p"))
            .build();
        return new Object[][]{
            {FriendshipEvent.builder().build(), FriendshipEvent.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().date(Tools.yesterday()).build(), filled, false},
            {filled.toBuilder().code(YOU_ACCEPTED_FRIEND_REQUEST).build(), filled, false},
            {filled.toBuilder().params(singletonList("updated")).build(), filled, false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
