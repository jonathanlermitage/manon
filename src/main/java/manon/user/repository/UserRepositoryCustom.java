package manon.user.repository;

import manon.user.UserNotFoundException;
import manon.user.form.UserFieldEnum;
import manon.user.registration.RegistrationStateEnum;

public interface UserRepositoryCustom {
    
    void updateField(String id, UserFieldEnum field, Object value) throws UserNotFoundException;
    
    /**
     * Add a friendship request between two users.
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    void askFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Create a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void acceptFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Reject a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void rejectFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * cancel a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void cancelFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Delete an existing friendship relation between two users.
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    void revokeFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Keep only a certain number of recent friendshipEvents on user.
     * @param id user id.
     * @param numberOfEventsToKeep number of recent friendshipEvents to keep.
     */
    void keepEvents(String id, int numberOfEventsToKeep) throws UserNotFoundException;
    
    void setPassword(String id, String password) throws UserNotFoundException;
    
    void setRegistrationState(String id, RegistrationStateEnum registrationState) throws UserNotFoundException;
}
