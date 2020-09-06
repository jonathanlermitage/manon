package manon.model.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPublicInfoTest {

    @Test
    void shouldVerifyToString() {
        assertThat(UserPublicInfo.builder().build().toString()).contains(
            "id", "username", "nickname");
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserPublicInfo.class)
            .verify();
    }
}
