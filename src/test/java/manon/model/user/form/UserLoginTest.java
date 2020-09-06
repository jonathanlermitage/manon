package manon.model.user.form;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginTest {

    @Test
    void shouldVerifyToString() {
        assertThat(UserLogin.builder().build().toString()).contains(
            "username", "password");
    }

    @Test
    public void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserLogin.class)
            .verify();
    }
}
