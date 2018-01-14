package manon.user.friendship.service;

import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.friendship.FriendshipExistsException;
import manon.user.friendship.FriendshipRequestExistsException;
import manon.user.friendship.FriendshipRequestNotFoundException;
import manon.user.repository.UserRepository;

public interface FriendshipService {
    
    /**
     * Keep only {@link User.Validation#MAX_EVENTS} recent friendshipEvents on user.
     * @param id user id.
     */
    void keepEvents(String id) throws UserNotFoundException;
    
    /**
     * Add a friendship request between two users.
     * @see UserRepository#askFriendship(String, String)
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    void askFriendship(String userIdFrom, String userIdTo)
            throws UserNotFoundException, FriendshipExistsException, FriendshipRequestExistsException;
    
    /**
     * Create a friendship request between two users.
     * @see UserRepository#acceptFriendshipRequest(String, String)
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void acceptFriendshipRequest(String userIdFrom, String userIdTo)
            throws UserNotFoundException, FriendshipRequestNotFoundException;
    
    /**
     * Reject a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @see UserRepository#rejectFriendshipRequest(String, String)
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void rejectFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Cancel a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @see UserRepository#acceptFriendshipRequest(String, String)
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void cancelFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException;
    
    /**
     * Delete a friendship relation between two users.
     * Don't fail if friendship doesn't exist, it won't corrupt data.
     * @see UserRepository#revokeFriendship(String, String)
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    void revokeFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException;
}
