package manon.document.user;

import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

public class UserEntityTest {

    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(UserEntity.builder().build().toString()).contains(
            "id", "username",
            "authorities", "password", "registrationState",
            "nickname", "email",
            "version", "creationDate", "updateDate");
    }

    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserEntity filled = UserEntity.builder()
            .id(1)
            .username("u")
            .authorities(UserRole.PLAYER.name())
            .password("apassword")
            .registrationState(RegistrationState.ACTIVE)
            .nickname("n")
            .email("e")
            .version(2)
            .creationDate(Tools.now())
            .updateDate(Tools.now())
            .build();
        return new Object[][]{
            {UserEntity.builder().build(), UserEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().authorities(UserRole.ADMIN.name()).build(), filled, false},
            {filled.toBuilder().password("newpassword").build(), filled, false},
            {filled.toBuilder().registrationState(RegistrationState.SUSPENDED).build(), filled, false},
            {filled.toBuilder().nickname("updated").build(), filled, false},
            {filled.toBuilder().email("updated").build(), filled, false},
            {filled.toBuilder().version(99).build(), filled, true},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true},
            {filled.toBuilder().updateDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserEntity o1, UserEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserEntity o1, UserEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    public void shouldVerifyPrePersistOnNew() {
        UserEntity o = UserEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
        Assertions.assertThat(o.getUpdateDate()).isEqualTo(o.getCreationDate());
    }

    @Test
    public void shouldVerifyPrePersistOnExisting() {
        UserEntity o = UserEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
        Assertions.assertThat(o.getUpdateDate()).isAfterOrEqualTo(creationDate);
    }
}
