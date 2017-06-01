package manon.matchmaking.repository;

import manon.matchmaking.document.MatchMaking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchMakingRepository extends MongoRepository<MatchMaking, String> {
}
