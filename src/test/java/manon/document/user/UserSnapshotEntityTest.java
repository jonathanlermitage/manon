package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UserSnapshotEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(UserSnapshotEntity.builder().build().toString()).contains(
            "id", "userUsername",
            "userAuthorities", "userPassword", "userRegistrationState",
            "userNickname", "userEmail",
            "userVersion", "creationDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        UserSnapshotEntity o = UserSnapshotEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        UserSnapshotEntity o = UserSnapshotEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    public void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(UserSnapshotEntity.class)
            .withIgnoredFields("id", "user", "creationDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
