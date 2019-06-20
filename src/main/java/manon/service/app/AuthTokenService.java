package manon.service.app;

import manon.document.app.AuthToken;
import manon.util.ExistForTesting;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tools to work with JWT references stored in database.
 * JWT instances are generated in memory by {@link manon.service.app.JwtTokenService}, and {@link AuthTokenService} is used
 * to store references to JWT instances in database or check their existence. Every valid JWT instance should be referenced in database.
 * To invalidate a non-expired JWT instance, remove its reference from database.
 */
public interface AuthTokenService {
    
    /**
     * Create a token reference in database.
     * @param username authenticated user's username.
     * @param expirationDate authenticated user's token expiration date.
     * @return token created in database.
     */
    AuthToken create(String username, LocalDateTime expirationDate);
    
    /**
     * Check if a token reference exists in database.
     * @param id token id.
     * @return {@code true} if exists, otherwise {@code false}.
     */
    boolean exists(long id);
    
    /**
     * Remove all tokens  from database for given user.
     * @param username user's username.
     */
    void removeUserTokens(String username);
    
    /**
     * Remove all expired tokens from database, for every user.
     */
    void removeAllExpired();
    
    @ExistForTesting(why = "AuthAdminWSIT")
    List<AuthToken> findAll();
    
    @ExistForTesting(why = "AuthAdminWSIT")
    long count();
    
    @ExistForTesting(why = "AbstractIT")
    void deleteAll();
}
