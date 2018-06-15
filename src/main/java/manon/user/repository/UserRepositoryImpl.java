package manon.user.repository;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.form.UserUpdateForm;
import manon.user.model.FriendshipEvent;
import manon.user.model.FriendshipEventCode;
import manon.user.model.RegistrationState;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    // FIXME updateFirst fails if id not found, no problem with updateMulti
    
    private final ReactiveMongoTemplate mongoTemplate;
    
    @Override
    public Mono<Void> update(String id, UserUpdateForm userUpdateForm) {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update()
                        .set(User.Field.NICKNAME, userUpdateForm.getNickname())
                        .set(User.Field.EMAIL, userUpdateForm.getEmail()),
                User.class).then();
    }
    
    @Override
    public Mono<Void> askFriendship(String userIdFrom, String userIdTo) {
        Mono<UpdateResult> res1 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .addToSet(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        Mono<UpdateResult> res2 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .addToSet(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_SENT_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        return res1.and(res2);
    }
    
    @Override
    public Mono<Void> acceptFriendshipRequest(String userIdFrom, String userIdTo) {
        Mono<UpdateResult> res1 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDS, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        Mono<UpdateResult> res2 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDS, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        return res1.and(res2);
    }
    
    @Override
    public Mono<Void> rejectFriendshipRequest(String userIdFrom, String userIdTo) {
        Mono<UpdateResult> res1 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        Mono<UpdateResult> res2 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        return res1.and(res2);
    }
    
    @Override
    public Mono<Void> cancelFriendshipRequest(String userIdFrom, String userIdTo) {
        Mono<UpdateResult> res1 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_FROM, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        Mono<UpdateResult> res2 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDSHIP_REQUESTS_TO, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        return res1.and(res2);
    }
    
    @Override
    public Mono<Void> revokeFriendship(String userIdFrom, String userIdTo) {
        Mono<UpdateResult> res1 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdTo)),
                new Update()
                        .pull(User.Field.FRIENDS, userIdFrom)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        Mono<UpdateResult> res2 = mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(userIdFrom)),
                new Update()
                        .pull(User.Field.FRIENDS, userIdTo)
                        .addToSet(User.Field.FRIENDSHIP_EVENTS, FriendshipEvent.builder()
                                .code(FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST)
                                .params(Collections.singletonList(userIdFrom))
                                .build()),
                User.class);
        return res1.and(res2);
    }
    
    @Override
    public Mono<Void> keepEvents(String id, int numberOfEventsToKeep) {
        User user = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), User.class).block();
        List<FriendshipEvent> friendshipEvents = user.getFriendshipEvents();
        int nbToRemove = friendshipEvents.size() - numberOfEventsToKeep;
        if (nbToRemove > 0) {
            return mongoTemplate.updateMulti(
                    Query.query(Criteria.where(User.Field.ID).is(id)),
                    new Update().set(User.Field.FRIENDSHIP_EVENTS, friendshipEvents.subList(nbToRemove, friendshipEvents.size())),
                    User.class).then();
        }
        return Mono.empty();
    }
    
    @Override
    public Mono<Void> setPassword(String id, String password) {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update().set(User.Field.PASSWORD, password),
                User.class).then();
    }
    
    @Override
    public Mono<Void> setRegistrationState(String id, RegistrationState registrationState) {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where(User.Field.ID).is(id)),
                new Update().set(User.Field.REGISTRATION_STATE, registrationState),
                User.class).then();
    }
}
