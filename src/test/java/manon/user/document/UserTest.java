package manon.user.document;

import manon.user.model.RegistrationState;
import manon.user.model.UserAuthority;
import manon.util.Tools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

import static manon.util.Tools.temporize;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(User.builder().build().toString()).contains(
            "id", "username",
            "authorities", "password", "registrationState",
            "nickname", "email",
            "version", "creationDate", "updateDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        User filled = User.builder()
            .id(1)
            .username("u")
            .authorities(UserAuthority.ROLE_PLAYER.name())
            .password("apassword")
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
            {filled.toBuilder().authorities(UserAuthority.ROLE_ADMIN.name()).build(), filled, false},
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
    public void shouldVerifyEquals(User o1, User o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(User o1, User o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
    
    @Test
    public void shouldVerifyPrePersistOnNew() {
        User o = User.builder().build();
        o.prePersist();
        assertThat(o.getCreationDate()).isNotNull();
        assertThat(o.getUpdateDate()).isEqualTo(o.getCreationDate());
    }
    
    @Test
    public void shouldVerifyPrePersistOnExisting() {
        User o = User.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();
        temporize();
        
        o.prePersist();
        assertThat(o.getCreationDate()).isEqualTo(creationDate);
        assertThat(o.getUpdateDate()).isAfterOrEqualTo(creationDate);
    }
}
