package manon.user.document;

import manon.util.Tools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;

import static manon.user.model.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipEventTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(FriendshipEvent.builder().build().toString()).contains(
            "id", "user", "friend", "code", "creationDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipEvent filled = FriendshipEvent.builder()
            .id(1)
            .code(TARGET_SENT_FRIEND_REQUEST)
            .user(User.builder().username("u").build())
            .friend(User.builder().username("f").build())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {FriendshipEvent.builder().build(), FriendshipEvent.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().code(YOU_ACCEPTED_FRIEND_REQUEST).build(), filled, false},
            {filled.toBuilder().user(User.builder().username("new u").build()).build(), filled, false},
            {filled.toBuilder().friend(User.builder().username("new f").build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(FriendshipEvent o1, FriendshipEvent o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
    
    @Test
    public void shouldVerifyPrePersistOnNew() {
        FriendshipEvent o = FriendshipEvent.builder().build();
        o.prePersist();
        assertThat(o.getCreationDate()).isNotNull();
    }
    
    @Test
    public void shouldVerifyPrePersistOnExisting() {
        FriendshipEvent o = FriendshipEvent.builder().build();
        o.prePersist();
        Date creationDate = o.getCreationDate();
        
        o.prePersist();
        assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
