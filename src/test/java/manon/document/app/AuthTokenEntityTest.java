package manon.document.app;

import manon.util.Tools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;

class AuthTokenEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(AuthTokenEntity.builder().build().toString()).contains(
            "id", "username",
            "expirationDate", "creationDate");
    }

    static Object[][] dataProviderShouldVerifyEqualsAndHashCode() {
        AuthTokenEntity filled = AuthTokenEntity.builder()
            .id(1)
            .username("u")
            .expirationDate(Tools.now())
            .creationDate(Tools.now())
            .build();
        return new Object[][]{
            {AuthTokenEntity.builder().build(), AuthTokenEntity.builder().build(), true},
            {filled.toBuilder().build(), filled, true},
            {filled.toBuilder().id(99).build(), filled, false},
            {filled.toBuilder().username("updated").build(), filled, false},
            {filled.toBuilder().expirationDate(Tools.yesterday()).build(), filled, false},
            {filled.toBuilder().creationDate(Tools.yesterday()).build(), filled, true}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyEquals(AuthTokenEntity o1, AuthTokenEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.equals(o2)).isEqualTo(expectedEqual);
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyEqualsAndHashCode")
    void shouldVerifyHashCode(AuthTokenEntity o1, AuthTokenEntity o2, boolean expectedEqual) {
        Assertions.assertThat(o1.hashCode() == o2.hashCode()).isEqualTo(expectedEqual);
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        AuthTokenEntity o = AuthTokenEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        AuthTokenEntity o = AuthTokenEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }
}
