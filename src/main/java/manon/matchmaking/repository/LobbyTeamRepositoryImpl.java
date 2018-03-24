package manon.matchmaking.repository;

import lombok.RequiredArgsConstructor;
import manon.matchmaking.document.LobbyTeam;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LobbyTeamRepositoryImpl implements LobbyTeamRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public LobbyTeam removeUserId(String id, String userId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where(LobbyTeam.Field.USER_IDS).in(userId)),
                new Update()
                        .pull(LobbyTeam.Field.USER_IDS, userId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public LobbyTeam addUserId(String id, String userId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where(LobbyTeam.Field.ID).is(id)),
                new Update()
                        .addToSet(LobbyTeam.Field.USER_IDS, userId),
                LobbyTeam.class);
        return mongoTemplate.findById(id, LobbyTeam.class);
    }
    
    @Override
    public void setLeader(String id, String userId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where(LobbyTeam.Field.ID).is(id)),
                new Update()
                        .set(LobbyTeam.Field.LEADER, userId),
                LobbyTeam.class);
    }
    
    @Override
    public void setReady(String id, boolean ready) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where(LobbyTeam.Field.ID).is(id)),
                new Update()
                        .set(LobbyTeam.Field.READY, ready),
                LobbyTeam.class);
    }
}
