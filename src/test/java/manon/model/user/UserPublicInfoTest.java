package manon.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPublicInfoTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserPublicInfo.builder().build().toString()).contains(
            "id", "username", "nickname");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserPublicInfo filled = UserPublicInfo.builder()
            .id(1)
            .username("u")
            .nickname("n")
            .build();
        return new Object[][]{
            {UserPublicInfo.builder().build(), UserPublicInfo.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().nickname("updated").build(), filled, false}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(UserPublicInfo o1, UserPublicInfo o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(UserPublicInfo o1, UserPublicInfo o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
