package manon.repository.app;

import manon.document.app.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    /** Delete all tokens of given user. */
    void deleteAllByUsername(String username);

    /** Delete all expired tokens. */
    void deleteAllByExpirationDateBefore(LocalDateTime date);

    @Query("select at.id from AuthToken at where at.username = :username")
    List<Long> findAllIdByUsername(@Param("username") String username);

    @Query("select at.id from AuthToken at where at.expirationDate < :date")
    List<Long> findAllByExpirationDateBefore(@Param("date") LocalDateTime date);
}
