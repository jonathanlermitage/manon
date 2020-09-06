package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FriendshipRequestEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipRequestEntity.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipRequestEntity o = FriendshipRequestEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipRequestEntity o = FriendshipRequestEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(FriendshipRequestEntity.class)
            .withIgnoredFields("id", "creationDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
