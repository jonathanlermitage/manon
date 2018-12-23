package manon.app.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import manon.app.config.Cfg;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class PasswordEncoderServiceImpl implements PasswordEncoderService {
    
    private final Cfg cfg;
    
    @Getter
    private PasswordEncoder encoder;
    
    @PostConstruct
    private void prepare() {
        encoder = new BCryptPasswordEncoder(cfg.getSecurityBcryptStrength());
    }
    
    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return rawPassword != null && encoder.matches(rawPassword, encodedPassword);
    }
}
