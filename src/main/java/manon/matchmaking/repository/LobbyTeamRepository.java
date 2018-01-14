package manon.matchmaking.repository;

import manon.matchmaking.document.LobbyTeam;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyTeamRepository extends MongoRepository<LobbyTeam, String>, LobbyTeamRepositoryCustom {
    
    @NotNull Optional<LobbyTeam> findById(@NotNull String id);
    
    Optional<LobbyTeam> findByUserIds(String userId);
}
