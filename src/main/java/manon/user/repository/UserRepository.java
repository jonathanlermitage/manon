package manon.user.repository;

import manon.user.document.User;
import manon.user.document.UserIdProjection;
import manon.user.document.UserVersionProjection;
import manon.user.model.RegistrationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    @Query("select u from User u where u.id = :id")
    Optional<UserVersionProjection> findVersionById(@Param("id") long id);
    
    @Query("select u from User u where u.username = :username")
    Optional<UserIdProjection> findVersionByUsername(@Param("username") String username);
    
    boolean existsByUsername(String username);
    
    @Modifying
    @Query("update User u set u.email = :email, u.nickname = :nickname where u.id = :id")
    void update(@Param("id") long id, @Param("email") String email, @Param("nickname") String nickname);
    
    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    void setPassword(@Param("id") long id, @Param("password") String password);
    
    @Modifying
    @Query("update User u set u.registrationState = :registrationState where u.id = :id")
    void setRegistrationState(@Param("id") long id, @Param("registrationState") RegistrationState registrationState);
}
