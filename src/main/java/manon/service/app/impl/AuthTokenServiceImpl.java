package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import manon.document.app.AuthToken;
import manon.repository.app.AuthTokenRepository;
import manon.service.app.AuthTokenService;
import manon.util.ExistForTesting;
import manon.util.Tools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthTokenServiceImpl implements AuthTokenService {
    
    private final AuthTokenRepository authTokenRepository;
    
    @Override
    public AuthToken create(String username, LocalDateTime expirationDate) {
        return authTokenRepository.save(AuthToken.builder()
            .username(username)
            .expirationDate(expirationDate)
            .build());
    }
    
    @Override
    public boolean exists(long id) {
        return authTokenRepository.existsById(id);
    }
    
    @Override
    public void removeUserTokens(String username) {
        authTokenRepository.deleteAllByUsername(username);
    }
    
    @Override
    public void removeAllExpired() {
        authTokenRepository.deleteAllByExpirationDateBefore(Tools.now());
    }
    
    @Override
    @ExistForTesting(why = "AuthAdminWSIT")
    public List<AuthToken> findAll() {
        return authTokenRepository.findAll();
    }
    
    @Override
    @ExistForTesting(why = "AuthAdminWSIT")
    public long count() {
        return authTokenRepository.count();
    }
    
    @Override
    @ExistForTesting(why = "AbstractIT")
    public void deleteAll() {
        authTokenRepository.deleteAll();
    }
}
