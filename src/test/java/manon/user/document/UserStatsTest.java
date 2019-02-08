package manon.user.document;

import manon.util.Tools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStatsTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(UserStats.builder().build().toString()).contains(
            "id", "nbUsers", "creationDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserStats filled = UserStats.builder()
            .id(1L)
            .nbUsers(2)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserStats.builder().build(), UserStats.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99L).build(), filled, false},
            {filled.toBuilder().nbUsers(99).build(), filled, false},
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
}
