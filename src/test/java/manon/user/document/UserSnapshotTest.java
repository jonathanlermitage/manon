package manon.user.document;

import manon.user.model.RegistrationState;
import manon.user.model.UserAuthority;
import manon.util.Tools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSnapshotTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserSnapshot.builder().build().toString()).contains(
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
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Object o1, Object o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
    
    @Test
    public void shouldVerifyPrePersistOnNew() {
        UserSnapshot o = UserSnapshot.builder().build();
        o.prePersist();
        assertThat(o.getCreationDate()).isNotNull();
    }
    
    @Test
    public void shouldVerifyPrePersistOnExisting() {
        UserSnapshot o = UserSnapshot.builder().build();
        o.prePersist();
        Date creationDate = o.getCreationDate();
    
        o.prePersist();
        assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
