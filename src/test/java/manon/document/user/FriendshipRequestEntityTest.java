package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

class FriendshipRequestEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipRequestEntity.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipRequestEntity filled = FriendshipRequestEntity.builder()
            .id(1)
            .creationDate(Tools.now())
            .requestFrom(UserEntity.builder().id(10).build())
            .requestTo(UserEntity.builder().id(11).build())
            .build();
        return new Object[][]{
            {FriendshipRequestEntity.builder().build(), FriendshipRequestEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().requestFrom(UserEntity.builder().id(20).build()).build(), filled, false},
            {filled.toBuilder().requestTo(UserEntity.builder().id(21).build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(FriendshipRequestEntity o1, FriendshipRequestEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(FriendshipRequestEntity o1, FriendshipRequestEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipRequestEntity o = FriendshipRequestEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipRequestEntity o = FriendshipRequestEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
