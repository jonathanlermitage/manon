package manon.model.user.form;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateFormTest {

    @Test
    void shouldVerifyToString() {
        assertThat(UserUpdateForm.builder().build().toString()).contains(
            "nickname", "email");
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserUpdateForm.class)
            .verify();
    }
}
