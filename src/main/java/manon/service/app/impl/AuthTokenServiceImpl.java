package manon.service.app.impl;

import lombok.RequiredArgsConstructor;
import manon.app.Globals;
import manon.document.app.AuthToken;
import manon.repository.app.AuthTokenRepository;
import manon.service.app.AuthTokenService;
import manon.util.ExistForTesting;
import manon.util.Tools;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthTokenServiceImpl implements AuthTokenService {
    
    private final AuthTokenRepository authTokenRepository;
    private final CacheManager cm;
    
    @Override
    public AuthToken create(String username, LocalDateTime expirationDate) {
        return authTokenRepository.save(AuthToken.builder()
            .username(username)
            .expirationDate(expirationDate)
            .build());
    }
    
    @Override
    @Cacheable(value = Globals.CacheNames.CACHE_TOKEN_EXISTENCE, key = "#id")
    public boolean exists(long id) {
        return authTokenRepository.existsById(id);
    }
    
    @Override
    public void removeUserTokens(String username) {
        authTokenRepository.findAllIdByUsername(username).forEach(this::evictTokenExistenceCache);
        authTokenRepository.deleteAllByUsername(username);
    }
    
    /** Evict given token existence from {@link Globals.CacheNames#CACHE_TOKEN_EXISTENCE} cache via code, not via annotation: this method
     * can be safely called from another method of this class (when called from an other method of this class, annotation based cache eviction
     * would not work). See https://spring.io/blog/2012/05/23/transactions-caching-and-aop-understanding-proxy-usage-in-spring for details on
     * proxy classes.*/
    @SuppressWarnings("ConstantConditions")
    private void evictTokenExistenceCache(long id) {
        cm.getCache(Globals.CacheNames.CACHE_TOKEN_EXISTENCE).evict(id);
    }
    
    @Override
    public void removeAllExpired() {
        LocalDateTime now = Tools.now();
        authTokenRepository.findAllByExpirationDateBefore(now).forEach(this::evictTokenExistenceCache);
        authTokenRepository.deleteAllByExpirationDateBefore(now);
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
    @CacheEvict(value = Globals.CacheNames.CACHE_TOKEN_EXISTENCE, allEntries = true)
    public void deleteAll() {
        authTokenRepository.deleteAll();
    }
    
    @Override
    @CacheEvict(value = Globals.CacheNames.CACHE_TOKEN_EXISTENCE, allEntries = true)
    public void evictAllCache() {
    }
}
