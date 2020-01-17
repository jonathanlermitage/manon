package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

public class FriendshipTest {

    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(Friendship.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }

    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        Friendship filled = Friendship.builder()
            .id(1)
            .requestFrom(User.builder().username("f").build())
            .requestTo(User.builder().username("t").build())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {Friendship.builder().build(), Friendship.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().requestFrom(User.builder().username("new f").build()).build(), filled, false},
            {filled.toBuilder().requestTo(User.builder().username("new t").build()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyEquals(Friendship o1, Friendship o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    public void shouldVerifyHashCode(Friendship o1, Friendship o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    public void shouldVerifyPrePersistOnNew() {
        Friendship o = Friendship.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    public void shouldVerifyPrePersistOnExisting() {
        Friendship o = Friendship.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
