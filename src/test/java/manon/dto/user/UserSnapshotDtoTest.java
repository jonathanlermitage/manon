package manon.dto.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserSnapshotDtoTest {

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserSnapshotDto.class)
            .verify();
    }
}
