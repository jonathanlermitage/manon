package manon.document.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UserStatsEntityTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(UserStatsEntity.builder().build().toString()).contains(
            "id", "nbUsers", "creationDate");
    }

    @Test
    void shouldVerifyPrePersistOnNew() {
        UserStatsEntity o = UserStatsEntity.builder().build();
        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isNotNull();
    }

    @Test
    void shouldVerifyPrePersistOnExisting() {
        UserStatsEntity o = UserStatsEntity.builder().build();
        o.prePersist();
        LocalDateTime creationDate = o.getCreationDate();

        o.prePersist();
        Assertions.assertThat(o.getCreationDate()).isEqualTo(creationDate);
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.forClass(UserStatsEntity.class)
            .withIgnoredFields("id", "creationDate")
            .verify();
    }
}
