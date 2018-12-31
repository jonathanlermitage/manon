package manon.user.document;

import manon.user.model.FriendshipEvent;
import manon.user.model.RegistrationState;
import manon.user.model.UserAuthority;
import manon.util.Tools;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

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
        User filled = User.builder()
            .id("1")
            .username("u")
            .roles(singletonList(UserAuthority.ROLE_PLAYER))
            .registrationState(RegistrationState.ACTIVE)
            .nickname("n")
            .email("e")
            .friendshipRequestsTo(singletonList("friendshipRequestsTo"))
            .friendshipRequestsFrom(singletonList("friendshipRequestsFrom"))
            .friends(singletonList("friends"))
            .friendshipEvents(singletonList(FriendshipEvent.builder().date(Tools.now()).build()))
            .version(2)
            .creationDate(Tools.now())
            .updateDate(Tools.now())
            .build();
        return new Object[][]{
            {User.builder().build(), User.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id("99").build(), filled, false},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().roles(singletonList(UserAuthority.ROLE_ADMIN)).build(), filled, false},
            {filled.toBuilder().registrationState(RegistrationState.SUSPENDED).build(), filled, false},
            {filled.toBuilder().nickname("updated").build(), filled, false},
            {filled.toBuilder().email("updated").build(), filled, false},
            {filled.toBuilder().friendshipRequestsTo(singletonList("updated")).build(), filled, false},
            {filled.toBuilder().friendshipRequestsFrom(singletonList("updated")).build(), filled, false},
            {filled.toBuilder().friends(singletonList("updated")).build(), filled, false},
            {filled.toBuilder().friendshipEvents(singletonList(FriendshipEvent.builder().date(Tools.yesterday()).build())).build(), filled, false},
            {filled.toBuilder().version(99).build(), filled, true},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
            {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(User o1, User o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(User o1, User o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
