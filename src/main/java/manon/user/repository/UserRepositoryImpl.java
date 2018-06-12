package manon.user.repository;

import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.form.UserUpdateForm;
import manon.user.model.FriendshipEvent;
import manon.user.model.FriendshipEventCode;
import manon.user.model.RegistrationState;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public void update(String id, UserUpdateForm userUpdateForm) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update()
                        .set(User.Field.NICKNAME, userUpdateForm.getNickname())
                        .set(User.Field.EMAIL, userUpdateForm.getEmail()),
                User.class);
    }
    
    @Override
    public void askFriendship(String userIdFrom, String userIdTo) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .addToSet(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .addToSet(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
    }
    
    @Override
    public void acceptFriendshipRequest(String userIdFrom, String userIdTo) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDS, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDS, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
    }
    
    @Override
    public void rejectFriendshipRequest(String userIdFrom, String userIdTo) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
    }
    
    @Override
    public void cancelFriendshipRequest(String userIdFrom, String userIdTo) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
    }
    
    @Override
    public void revokeFriendship(String userIdFrom, String userIdTo) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDS, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDS, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
    }
    
    @Override
    public void keepEvents(String id, int numberOfEventsToKeep) {
        User user = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), User.class);
        List<FriendshipEvent> friendshipEvents = user.getFriendshipEvents();
        int nbToRemove = friendshipEvents.size() - numberOfEventsToKeep;
        if (nbToRemove > 0) {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where(User.Field.ID).is(id)),
                    new Update().set(User.Field.FRIENDSHIP_EVENTS, friendshipEvents.subList(nbToRemove, friendshipEvents.size())),
                    User.class);
        }
    }
    
    @Override
    public void setPassword(String id, String password) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update().set(User.Field.PASSWORD, password),
                User.class).getMatchedCount();
    }
    
    @Override
    public void setRegistrationState(String id, RegistrationState registrationState) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update().set(User.Field.REGISTRATION_STATE, registrationState),
                User.class).getMatchedCount();
    }
}
