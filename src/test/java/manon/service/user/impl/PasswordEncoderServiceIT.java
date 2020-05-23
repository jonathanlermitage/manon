package manon.service.user.impl;

import manon.util.basetest.AbstractNoUserIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordEncoderServiceIT extends AbstractNoUserIT {

    @Test
    void shouldEncode() {
        String password = "foo";
        assertThat(passwordEncoderService.encode(password)).isNotBlank().isNotEqualTo(password);
    }

    @Test
    void shouldFailToEncodeNull() {
        assertThatThrownBy(() -> passwordEncoderService.encode(null))
            .isInstanceOf(Exception.class);
    }

    @Test
    void shouldMatch() {
        String password = "foo";
        String encodedPassword = passwordEncoderService.encode(password);
        assertThat(passwordEncoderService.matches(password, encodedPassword)).isTrue();
    }

    @Test
    void shouldNotMatch() {
        String password = "foo";
        assertThat(passwordEncoderService.matches(password, password)).isFalse();
    }

    @Test
    void shouldGetEncoder() {
        assertThat(passwordEncoderService.getEncoder()).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }
}
