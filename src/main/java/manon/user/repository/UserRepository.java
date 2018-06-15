package manon.user.repository;

import manon.user.document.User;
import manon.user.document.UserIdProjection;
import manon.user.document.UserVersionProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String>, UserRepositoryCustom {
    
    Mono<User> findByUsername(String username);
    
    @Query(value = "{'id':?0 }")
    Mono<UserVersionProjection> findVersionById(@NotNull String id);
    
    @Query(value = "{'username':?0 }")
    Mono<UserIdProjection> findVersionByUsername(@NotNull String username);
    
    Mono<Boolean> existsUserByUsername(String username); // FIXME returns null when exist is false
    
    Mono<Long> countByUsername(String username);
}
