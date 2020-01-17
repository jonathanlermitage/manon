package manon.service.user.impl;

import manon.util.basetest.AbstractNoUserIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PasswordEncoderServiceIT extends AbstractNoUserIT {

    @Test
    public void shouldEncode() {
        String password = "foo";
        assertThat(passwordEncoderService.encode(password)).isNotBlank().isNotEqualTo(password);
    }

    @Test
    public void shouldFailToEncodeNull() {
        assertThatThrownBy(() -> passwordEncoderService.encode(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldMatch() {
        String password = "foo";
        String encodedPassword = passwordEncoderService.encode(password);
        assertThat(passwordEncoderService.matches(password, encodedPassword)).isTrue();
    }

    @Test
    public void shouldNotMatch() {
        String password = "foo";
        assertThat(passwordEncoderService.matches(password, password)).isFalse();
    }

    @Test
    public void shouldGetEncoder() {
        assertThat(passwordEncoderService.getEncoder()).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }
}
