package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

class FriendshipEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipEntity.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipEntity filled = FriendshipEntity.builder()
            .id(1)
            .requestFrom(UserEntity.builder().username("f").build())
            .requestTo(UserEntity.builder().username("t").build())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {FriendshipEntity.builder().build(), FriendshipEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().requestFrom(UserEntity.builder().username("new f").build()).build(), filled, false},
            {filled.toBuilder().requestTo(UserEntity.builder().username("new t").build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(FriendshipEntity o1, FriendshipEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(FriendshipEntity o1, FriendshipEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipEntity o = FriendshipEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipEntity o = FriendshipEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
