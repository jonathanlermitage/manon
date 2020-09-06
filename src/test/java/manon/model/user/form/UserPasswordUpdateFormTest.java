package manon.model.user.form;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPasswordUpdateFormTest {

    @Test
    void shouldVerifyToString() {
        assertThat(UserPasswordUpdateForm.builder().build().toString()).contains(
            "oldPassword", "newPassword");
    }

    @Test
    public void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(UserPasswordUpdateForm.class)
            .verify();
    }
}
