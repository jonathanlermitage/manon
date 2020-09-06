package manon.dto.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FriendshipRequestDtoTest {

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(FriendshipRequestDto.class)
            .verify();
    }
}
