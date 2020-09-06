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
            "nickname", "email",
            "version", "creationDate", "updateDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        UserEntity o = UserEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
        Assertions.assertThat(o.getUpdateDate()).isEqualTo(o.getCreationDate());
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        UserEntity o = UserEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
        Assertions.assertThat(o.getUpdateDate()).isAfterOrEqualTo(creationDate);
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(UserEntity.class)
            .withIgnoredFields("id", "userSnapshots", "version", "creationDate", "updateDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
