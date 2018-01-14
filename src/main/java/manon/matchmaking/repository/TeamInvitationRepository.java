package manon.matchmaking.repository;

import manon.matchmaking.document.TeamInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamInvitationRepository extends MongoRepository<TeamInvitation, String> {
    
    Optional<TeamInvitation> findByUserIdAndTeamId(String userId, String teamId);
    
    List<TeamInvitation> findByUserId(String userId);
}
