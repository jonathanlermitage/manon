package manon.profile.repository;

import com.mongodb.WriteResult;
import manon.profile.ProfileFieldEnum;
import manon.profile.ProfileNotFoundException;
import manon.profile.ProfileStateEnum;
import manon.profile.document.Profile;
import manon.profile.friendship.FriendshipEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ProfileRepositoryImpl implements ProfileRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Autowired
    public ProfileRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public void updateField(String id, ProfileFieldEnum field, Object value) throws ProfileNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set(field.getFieldname(), value),
                Profile.class).getN()) {
            throw new ProfileNotFoundException(id);
        }
    }
    
    @Override
    public void askFriendship(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException {
        verify(profileIdTo, mongoTemplate.updateFirst(
                query(where("id").is(profileIdTo)),
                new Update()
                        .addToSet("friendshipRequestsFrom", profileIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
        verify(profileIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(profileIdFrom)),
                new Update()
                        .addToSet("friendshipRequestsTo", profileIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
    }
    
    @Override
    public void acceptFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException {
        verify(profileIdTo, mongoTemplate.updateFirst(
                query(where("id").is(profileIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", profileIdFrom)
                        .addToSet("friends", profileIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
        verify(profileIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(profileIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", profileIdTo)
                        .addToSet("friends", profileIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
    }
    
    @Override
    public void rejectFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException {
        verify(profileIdTo, mongoTemplate.updateFirst(
                query(where("id").is(profileIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", profileIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
        verify(profileIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(profileIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", profileIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
    }
    
    @Override
    public void cancelFriendshipRequest(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException {
        verify(profileIdTo, mongoTemplate.updateFirst(
                query(where("id").is(profileIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", profileIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
        verify(profileIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(profileIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", profileIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
    }
    
    @Override
    public void revokeFriendship(String profileIdFrom, String profileIdTo) throws ProfileNotFoundException {
        verify(profileIdTo, mongoTemplate.updateFirst(
                query(where("id").is(profileIdTo)),
                new Update()
                        .pull("friends", profileIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
        verify(profileIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(profileIdFrom)),
                new Update()
                        .pull("friends", profileIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(profileIdFrom))
                                .build()),
                Profile.class));
    }
    
    @Override
    public void keepEvents(String id, int numberOfEventsToKeep) throws ProfileNotFoundException {
        Profile profile = mongoTemplate.findOne(query(where("id").is(id)), Profile.class);
        if (null == profile) {
            throw new ProfileNotFoundException(id);
        }
        List<FriendshipEvent> friendshipEvents = profile.getFriendshipEvents();
        int nbToRemove = friendshipEvents.size() - numberOfEventsToKeep;
        if (nbToRemove > 0) {
            verify(id, mongoTemplate.updateFirst(
                    query(where("id").is(id)),
                    new Update()
                            .set("friendshipEvents", friendshipEvents.subList(nbToRemove, friendshipEvents.size())),
                    Profile.class));
        }
    }
    
    @Override
    public void setState(String id, ProfileStateEnum state) throws ProfileNotFoundException {
        verify(id, mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update()
                        .set("state", state),
                Profile.class));
    }
    
    @Override
    public long getSkill(String id) {
        Profile profile = mongoTemplate.findOne(query(where("id").is(id)), Profile.class);
        return null == profile ? 0L : profile.getSkill();
    }
    
    @Override
    public long sumSkill(Collection<String> ids) {
        List<Profile> profiles = mongoTemplate.find(query(where("id").in(ids)), Profile.class);
        return profiles.stream().map(Profile::getSkill).mapToLong(Long::longValue).sum();
    }
    
    private void verify(String id, WriteResult writeResult) throws ProfileNotFoundException {
        if (0 == writeResult.getN()) {
            throw new ProfileNotFoundException(id);
        }
    }
}
