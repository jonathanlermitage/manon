package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FriendshipEventEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipEventEntity.builder().build().toString()).contains(
            "id", "user", "friend", "code", "creationDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipEventEntity o = FriendshipEventEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipEventEntity o = FriendshipEventEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    public void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(FriendshipEventEntity.class)
            .withIgnoredFields("id", "creationDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
