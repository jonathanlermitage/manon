package manon.user.document;

import manon.util.Tools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipRequestTest {
    
    @Test
    public void shouldVerifyToString() {
        assertThat(FriendshipRequest.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }
    
    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        FriendshipRequest filled = FriendshipRequest.builder()
            .id(1)
            .creationDate(Tools.now())
            .requestFrom(User.builder().id(10).build())
            .requestTo(User.builder().id(11).build())
            .build();
        return new Object[][]{
            {FriendshipRequest.builder().build(), FriendshipRequest.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().requestFrom(User.builder().id(20).build()).build(), filled, false},
            {filled.toBuilder().requestTo(User.builder().id(21).build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(FriendshipRequest o1, FriendshipRequest o2, boolean expectedEqual) {
        assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(FriendshipRequest o1, FriendshipRequest o2, boolean expectedEqual) {
        assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }
}
