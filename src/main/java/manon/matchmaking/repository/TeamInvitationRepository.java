package manon.matchmaking.repository;

import manon.matchmaking.TeamInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamInvitationRepository extends MongoRepository<TeamInvitation, String> {
    
    Optional<TeamInvitation> findByProfileIdAndTeamId(String profileId, String teamId);
    List<TeamInvitation> findByProfileId(String profileId);
}
