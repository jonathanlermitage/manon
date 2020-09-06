package manon.dto.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserWithSnapshotsDtoTest {

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserWithSnapshotsDto.class)
            .withIgnoredFields("id", "creationDate", "updateDate")
            .verify();
    }
}
