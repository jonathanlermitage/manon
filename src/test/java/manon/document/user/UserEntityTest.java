package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UserEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(UserEntity.builder().build().toString()).contains(
            "id", "username",
            "authorities", "password", "registrationState",
            "nickname", "email", "creationDate");
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(UserEntity.class)
            .withIgnoredFields("id", "userSnapshots", "creationDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
