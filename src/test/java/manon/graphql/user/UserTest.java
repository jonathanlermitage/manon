package manon.graphql.user;

import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UserTest {
    
    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(User.builder().build().toString()).contains(
            "id", "username",
            "authorities", "registrationState",
            "nickname", "email",
            "version", "creationDate", "updateDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        User filled = User.builder()
            .id(1)
            .username("u")
            .authorities(UserRole.PLAYER.name())
            .registrationState(RegistrationState.ACTIVE)
            .nickname("n")
            .email("e")
            .version(2)
            .creationDate(Tools.now())
            .updateDate(Tools.now())
            .build();
        return new Object[][]{
            {User.builder().build(), User.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().authorities(UserRole.ADMIN.name()).build(), filled, false},
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
    public void shouldVerifyEquals(User o1, User o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(User o1, User o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
    
}
