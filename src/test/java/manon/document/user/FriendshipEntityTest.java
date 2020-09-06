package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FriendshipEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(FriendshipEntity.builder().build().toString()).contains(
            "id", "requestFrom", "requestTo", "creationDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        FriendshipEntity o = FriendshipEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        FriendshipEntity o = FriendshipEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(FriendshipEntity.class)
            .withIgnoredFields("id", "creationDate")
            .withPrefabValues(UserEntity.class,
                UserEntity.builder().nickname("n1").build(),
                UserEntity.builder().nickname("n2").build())
            .verify();
    }
}
