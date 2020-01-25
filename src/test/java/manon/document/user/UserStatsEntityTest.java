package manon.document.user;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

public class UserStatsEntityTest {

    @Test
    public void shouldVerifyToString() {
        Assertions.assertThat(UserStatsEntity.builder().build().toString()).contains(
            "id", "nbUsers", "creationDate");
    }

    public static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        UserStatsEntity filled = UserStatsEntity.builder()
            .id(1L)
            .nbUsers(2)
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {UserStatsEntity.builder().build(), UserStatsEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99L).build(), filled, false},
            {filled.toBuilder().nbUsers(99).build(), filled, false},
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
        UserStatsEntity o = UserStatsEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    public void shouldVerifyPrePersistOnExisting() {
        UserStatsEntity o = UserStatsEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
