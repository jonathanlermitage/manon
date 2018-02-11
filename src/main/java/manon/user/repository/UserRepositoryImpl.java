package manon.user.repository;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.form.UserFieldEnum;
import manon.user.friendship.model.FriendshipEvent;
import manon.user.registration.RegistrationStateEnum;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public void updateField(String id, UserFieldEnum field, Object value) throws UserNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set(field.getFieldname(), value),
                User.class).getMatchedCount()) {
            throw new UserNotFoundException(id);
        }
    }
    
    @Override
    public void askFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException {
        verify(userIdTo, mongoTemplate.updateFirst(
                query(where("id").is(userIdTo)),
                new Update()
                        .addToSet("friendshipRequestsFrom", userIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
        verify(userIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(userIdFrom)),
                new Update()
                        .addToSet("friendshipRequestsTo", userIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
    }
    
    @Override
    public void acceptFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException {
        verify(userIdTo, mongoTemplate.updateFirst(
                query(where("id").is(userIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", userIdFrom)
                        .addToSet("friends", userIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_ACCEPTED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
        verify(userIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(userIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", userIdTo)
                        .addToSet("friends", userIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_ACCEPTED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
    }
    
    @Override
    public void rejectFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException {
        verify(userIdTo, mongoTemplate.updateFirst(
                query(where("id").is(userIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", userIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_REJECTED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
        verify(userIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(userIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", userIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_REJECTED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
    }
    
    @Override
    public void cancelFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException {
        verify(userIdTo, mongoTemplate.updateFirst(
                query(where("id").is(userIdTo)),
                new Update()
                        .pull("friendshipRequestsFrom", userIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_CANCELED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
        verify(userIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(userIdFrom)),
                new Update()
                        .pull("friendshipRequestsTo", userIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_CANCELED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
    }
    
    @Override
    public void revokeFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException {
        verify(userIdTo, mongoTemplate.updateFirst(
                query(where("id").is(userIdTo)),
                new Update()
                        .pull("friends", userIdFrom)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.TARGET_REVOKED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
        verify(userIdFrom, mongoTemplate.updateFirst(
                query(where("id").is(userIdFrom)),
                new Update()
                        .pull("friends", userIdTo)
                        .addToSet("friendshipEvents", FriendshipEvent.builder()
                                .event(FriendshipEvent.Code.YOU_REVOKED_FRIEND_REQUEST)
                                .params(List.of(userIdFrom))
                                .build()),
                User.class));
    }
    
    @Override
    public void keepEvents(String id, int numberOfEventsToKeep) throws UserNotFoundException {
        User user = mongoTemplate.findOne(query(where("id").is(id)), User.class);
        List<FriendshipEvent> friendshipEvents = user.getFriendshipEvents();
        int nbToRemove = friendshipEvents.size() - numberOfEventsToKeep;
        if (nbToRemove > 0) {
            verify(id, mongoTemplate.updateFirst(
                    query(where("id").is(id)),
                    new Update()
                            .set("friendshipEvents", friendshipEvents.subList(nbToRemove, friendshipEvents.size())),
                    User.class));
        }
    }
    
    @Override
    public void setPassword(String id, String password) throws UserNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set("password", password),
                User.class).getMatchedCount()) {
            throw new UserNotFoundException(id);
        }
    }
    
    @Override
    public void setRegistrationState(String id, RegistrationStateEnum registrationState) throws UserNotFoundException {
        if (0 == mongoTemplate.updateFirst(
                query(where("id").is(id)),
                new Update().set("registrationState", registrationState),
                User.class).getMatchedCount()) {
            throw new UserNotFoundException(id);
        }
    }
    
    private void verify(String id, UpdateResult updateResult) throws UserNotFoundException {
        if (0 == updateResult.getMatchedCount()) {
            throw new UserNotFoundException(id);
        }
    }
}
