package manon.app.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PasswordEncoderServiceImpl implements PasswordEncoderService {
    
    @Value("${manon.security.bcrypt.strength}")
    private Integer bcryptStrength;
    
    @Getter
    private PasswordEncoder encoder;
    
    @PostConstruct
    private void prepare() {
        encoder = new BCryptPasswordEncoder(bcryptStrength);
    }
    
    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }
}
