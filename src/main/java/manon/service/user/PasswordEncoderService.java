package manon.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordEncoderService {

    String encode(String password);

    PasswordEncoder getEncoder();

    boolean matches(String rawPassword, String encodedPassword);
}
