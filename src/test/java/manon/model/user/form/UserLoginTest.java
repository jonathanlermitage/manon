package manon.model.user.form;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UserLoginTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserLogin.builder().build().toString()).contains(
            "username", "password");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserLogin filled = UserLogin.builder()
            .username("u")
            .password("p")
            .build();
        return new Object[][]{
            {UserLogin.builder().build(), UserLogin.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().password("updated").build(), filled, false}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserLogin o1, UserLogin o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserLogin o1, UserLogin o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
