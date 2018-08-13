package manon.user.repository;

import manon.user.document.User;
import manon.user.document.UserIdProjection;
import manon.user.document.UserVersionProjection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    
    Optional<User> findByUsername(String username);
    
    @Query(value = "{'id':?0 }")
    Optional<UserVersionProjection> findVersionById(String id);
    
    @Query(value = "{'username':?0 }")
    Optional<UserIdProjection> findVersionByUsername(String username);
    
    @Query(value = "{'username':?0 }", exists = true)
    boolean usernameExists(String username);
}
