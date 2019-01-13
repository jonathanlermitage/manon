package manon.user.form;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationFormTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(RegistrationForm.builder().build().toString()).contains(
            "username", "password");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        RegistrationForm filled = RegistrationForm.builder()
            .username("u")
            .password("p")
            .build();
        return new Object[][]{
            {RegistrationForm.builder().build(), RegistrationForm.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().password("updated").build(), filled, false}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(RegistrationForm o1, RegistrationForm o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(RegistrationForm o1, RegistrationForm o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
