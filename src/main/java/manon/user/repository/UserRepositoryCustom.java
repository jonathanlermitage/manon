package manon.user.repository;

import manon.user.form.UserUpdateForm;
import manon.user.registration.RegistrationState;

public interface UserRepositoryCustom {
    
    void update(String id, UserUpdateForm userUpdateForm);
    
    /**
     * Add a friendship request between two users.
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    void askFriendship(String userIdFrom, String userIdTo);
    
    /**
     * Create a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void acceptFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Reject a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void rejectFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * cancel a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void cancelFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Delete an existing friendship relation between two users.
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    void revokeFriendship(String userIdFrom, String userIdTo);
    
    /**
     * Keep only a certain number of recent friendshipEvents on user.
     * @param id user id.
     * @param numberOfEventsToKeep number of recent friendshipEvents to keep.
     */
    void keepEvents(String id, int numberOfEventsToKeep);
    
    void setPassword(String id, String password);
    
    void setRegistrationState(String id, RegistrationState registrationState);
}
