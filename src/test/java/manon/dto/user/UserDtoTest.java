package manon.dto.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class UserDtoTest {

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserDto.class)
            .withIgnoredFields("id", "creationDate", "updateDate")
            .verify();
    }
}
