package manon.service.user.impl;

import lombok.Getter;
import manon.app.Cfg;
import manon.service.user.PasswordEncoderService;
import manon.util.HashUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Getter
@Service
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    private final PasswordEncoder encoder;

    public PasswordEncoderServiceImpl(Cfg cfg) {
        encoder = new BCryptPasswordEncoder(cfg.getSecurityBcryptStrength(), new SecureRandom());
    }

    @Override
    public String encode(String password) {
        return encoder.encode(HashUtils.hashRawPassword(password));
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null && encoder.matches(HashUtils.hashRawPassword(rawPassword), encodedPassword);
    }
}
