package manon.matchmaking.repository;

import manon.matchmaking.document.LobbySolo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbySoloRepository extends MongoRepository<LobbySolo, String> {
    
    Optional<LobbySolo> findByProfileId(String profileId);
    
    void removeByProfileId(String profileId);
}
