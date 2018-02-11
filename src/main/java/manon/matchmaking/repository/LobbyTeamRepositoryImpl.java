package manon.matchmaking.repository;

import lombok.RequiredArgsConstructor;
import manon.matchmaking.document.LobbyTeam;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class LobbyTeamRepositoryImpl implements LobbyTeamRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public LobbyTeam removeUserId(String id, String userId) {
        mongoTemplate.updateMulti(
                query(where("userIds").in(userId)),
                new Update()
                        .pull("userIds", userId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public LobbyTeam addUserId(String id, String userId) {
        mongoTemplate.updateMulti(
                query(where("id").is(id)),
                new Update()
                        .addToSet("userIds", userId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public void setLeader(String id, String userId) {
        mongoTemplate.updateMulti(
                query(where("id").is(id)),
                new Update()
                        .set("leader", userId),
                LobbyTeam.class);
    }
    
    @Override
    public void setReady(String id, boolean ready) {
        mongoTemplate.updateMulti(
                query(where("id").is(id)),
                new Update()
                        .set("ready", ready),
                LobbyTeam.class);
    }
}
