package manon.matchmaking.repository;

import manon.matchmaking.document.LobbyTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class LobbyTeamRepositoryImpl implements LobbyTeamRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Autowired
    public LobbyTeamRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public long unsetProfileId(String profileId) {
        return mongoTemplate.updateMulti(
                query(where("profileIds").in(profileId)),
                new Update()
                        .pull("profileIds", profileId),
                LobbyTeam.class).getN();
    }
}
