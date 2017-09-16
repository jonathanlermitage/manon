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
    public LobbyTeam removeProfileId(String id, String profileId) {
        mongoTemplate.updateMulti(
                query(where("profileIds").in(profileId)),
                new Update()
                        .pull("profileIds", profileId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public LobbyTeam addProfileId(String id, String profileId) {
        mongoTemplate.updateMulti(
                query(where("id").is(id)),
                new Update()
                        .addToSet("profileIds", profileId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public void setLeader(String id, String profileId) {
        mongoTemplate.updateMulti(
                query(where("id").is(id)),
                new Update()
                        .set("leader", profileId),
                LobbyTeam.class);
    }
}
