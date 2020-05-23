package manon.model.user.form;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateFormTest {

    @Test
    void shouldVerifyToString() {
        assertThat(UserUpdateForm.builder().build().toString()).contains(
            "nickname", "email");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserUpdateForm filled = UserUpdateForm.builder()
            .nickname("n")
            .email("e")
            .build();
        return new Object[][]{
            {UserUpdateForm.builder().build(), UserUpdateForm.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().nickname("updated").build(), filled, false},
            {filled.toBuilder().email("updated").build(), filled, false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(UserUpdateForm o1, UserUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(UserUpdateForm o1, UserUpdateForm o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
