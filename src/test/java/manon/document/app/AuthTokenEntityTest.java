package manon.document.app;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class AuthTokenEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(AuthTokenEntity.builder().build().toString()).contains(
            "id", "username",
            "expirationDate", "creationDate");
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

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(AuthTokenEntity.class)
            .withIgnoredFields("id", "creationDate")
            .verify();
    }
}
