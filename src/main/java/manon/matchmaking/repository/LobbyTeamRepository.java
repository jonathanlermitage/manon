package manon.matchmaking.repository;

import manon.matchmaking.document.LobbyTeam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyTeamRepository extends MongoRepository<LobbyTeam, String>, LobbyTeamRepositoryCustom {
    
    Optional<LobbyTeam> findById(String id);
    Optional<LobbyTeam> findByProfileIds(String profileId);
}
