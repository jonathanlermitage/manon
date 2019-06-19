package manon.repository.app;

import manon.document.app.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    
    /** Delete all tokens of given user. */
    void deleteAllByUsername(String username);
    
    /** Delete all expired tokens. */
    void deleteAllByExpirationDateBefore(LocalDateTime date);
}
