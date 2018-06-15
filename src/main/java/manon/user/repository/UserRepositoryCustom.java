package manon.user.repository;

import manon.user.form.UserUpdateForm;
import manon.user.model.RegistrationState;
import reactor.core.publisher.Mono;

public interface UserRepositoryCustom {
    
    Mono<Void> update(String id, UserUpdateForm userUpdateForm);
    
    /**
     * Add a friendship request between two users.
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    Mono<Void> askFriendship(String userIdFrom, String userIdTo);
    
    /**
     * Create a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    Mono<Void> acceptFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Reject a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    Mono<Void> rejectFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * cancel a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    Mono<Void> cancelFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Delete an existing friendship relation between two users.
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    Mono<Void> revokeFriendship(String userIdFrom, String userIdTo);
    
    /**
     * Keep only a certain number of recent friendshipEvents on user.
     * @param id user id.
     * @param numberOfEventsToKeep number of recent friendshipEvents to keep.
     */
    Mono<Void> keepEvents(String id, int numberOfEventsToKeep);
    
    Mono<Void> setPassword(String id, String password);
    
    Mono<Void> setRegistrationState(String id, RegistrationState registrationState);
}
