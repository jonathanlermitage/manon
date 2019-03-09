package manon.document.user;

import manon.model.user.RegistrationState;
import manon.model.user.UserAuthority;
import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

public class UserSnapshotTest {
    
    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(UserSnapshot.builder().build().toString()).contains(
            "id", "userId", "userUsername",
            "userAuthorities", "userPassword", "userRegistrationState",
            "userNickname", "userEmail",
            "userVersion", "creationDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserSnapshot filled = UserSnapshot.builder()
            .id(1)
            .userId(2)
            .userUsername("u")
            .userAuthorities(UserAuthority.ROLE_PLAYER.name())
            .userPassword("apassword")
            .userRegistrationState(RegistrationState.ACTIVE)
            .userNickname("n")
            .userEmail("e")
            .userVersion(2)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserStats.builder().build(), UserStats.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().userId(100).build(), filled, false},
            {filled.toBuilder().userUsername("updated").build(), filled, false},
            {filled.toBuilder().userAuthorities(UserAuthority.ROLE_ADMIN.name()).build(), filled, false},
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
    public void shouldVerifyEquals(Object o1, Object o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Object o1, Object o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
    
    @Test
    public void shouldVerifyPrePersistOnNew() {
        UserSnapshot o = UserSnapshot.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }
    
    @Test
    public void shouldVerifyPrePersistOnExisting() {
        UserSnapshot o = UserSnapshot.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();
        
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
