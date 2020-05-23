package manon.document.user;

import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

class UserSnapshotEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(UserSnapshotEntity.builder().build().toString()).contains(
            "id", "userUsername",
            "userAuthorities", "userPassword", "userRegistrationState",
            "userNickname", "userEmail",
            "userVersion", "creationDate");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserSnapshotEntity filled = UserSnapshotEntity.builder()
            .id(1)
            .user(UserEntity.builder().id(2).build())
            .userUsername("u")
            .userAuthorities(UserRole.PLAYER.name())
            .userPassword("apassword")
            .userRegistrationState(RegistrationState.ACTIVE)
            .userNickname("n")
            .userEmail("e")
            .userVersion(2)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserSnapshotEntity.builder().build(), UserSnapshotEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().user(UserEntity.builder().id(100).build()).build(), filled, true},
            {filled.toBuilder().userUsername("updated").build(), filled, false},
            {filled.toBuilder().userAuthorities(UserRole.ADMIN.name()).build(), filled, false},
            {filled.toBuilder().userPassword("newpassword").build(), filled, false},
            {filled.toBuilder().userRegistrationState(RegistrationState.SUSPENDED).build(), filled, false},
            {filled.toBuilder().userNickname("updated").build(), filled, false},
            {filled.toBuilder().userEmail("updated").build(), filled, false},
            {filled.toBuilder().userVersion(99).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(Object o1, Object o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(Object o1, Object o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        UserSnapshotEntity o = UserSnapshotEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        UserSnapshotEntity o = UserSnapshotEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
