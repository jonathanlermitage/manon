package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

public class FriendshipRequestTest {

    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(FriendshipRequest.builder().build().toString()).contains(
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
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(FriendshipRequest o1, FriendshipRequest o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    public void shouldVerifyPrePersistOnNew() {
        FriendshipRequest o = FriendshipRequest.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    public void shouldVerifyPrePersistOnExisting() {
        FriendshipRequest o = FriendshipRequest.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
