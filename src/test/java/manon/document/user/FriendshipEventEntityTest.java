package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

import static manon.model.user.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST;

class FriendshipEventEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipEventEntity.builder().build().toString()).contains(
            "id", "user", "friend", "code", "creationDate");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipEventEntity filled = FriendshipEventEntity.builder()
            .id(1)
            .code(TARGET_SENT_FRIEND_REQUEST)
            .user(UserEntity.builder().username("u").build())
            .friend(UserEntity.builder().username("f").build())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {FriendshipEventEntity.builder().build(), FriendshipEventEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().code(YOU_ACCEPTED_FRIEND_REQUEST).build(), filled, false},
            {filled.toBuilder().user(UserEntity.builder().username("new u").build()).build(), filled, false},
            {filled.toBuilder().friend(UserEntity.builder().username("new f").build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(FriendshipEventEntity o1, FriendshipEventEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(FriendshipEventEntity o1, FriendshipEventEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipEventEntity o = FriendshipEventEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipEventEntity o = FriendshipEventEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
