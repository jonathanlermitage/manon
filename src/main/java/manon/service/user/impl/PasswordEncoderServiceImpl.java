package manon.service.user.impl;

import lombok.Getter;
import manon.app.Cfg;
import manon.service.user.PasswordEncoderService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    @Getter
    private PasswordEncoder encoder;

    public PasswordEncoderServiceImpl(Cfg cfg) {
        encoder = new BCryptPasswordEncoder(cfg.getSecurityBcryptStrength());
    }

    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null && encoder.matches(rawPassword, encodedPassword);
    }
}
