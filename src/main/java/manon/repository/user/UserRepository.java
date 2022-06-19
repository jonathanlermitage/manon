package manon.repository.user;

import manon.document.user.UserEntity;
import manon.document.user.UserIdProjection;
import manon.document.user.UserVersionProjection;
import manon.model.user.RegistrationState;
import manon.repository.WorkaroundUntilHibernate6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
    extends JpaRepository<UserEntity, Long>,
    QuerydslPredicateExecutor<UserEntity>,
    WorkaroundUntilHibernate6<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

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

    @Query("select u from User u left join fetch u.userSnapshots where u.id = :id")
    Optional<UserEntity> findAndFetchUserSnapshots(@Param("id") long id);
}
