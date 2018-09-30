package manon.user.service;

import manon.user.err.FriendshipExistsException;
import manon.user.err.FriendshipRequestExistsException;
import manon.user.err.FriendshipRequestNotFoundException;
import manon.user.err.UserNotFoundException;
import manon.user.repository.UserRepository;

public interface FriendshipService {
    
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
    void rejectFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Cancel a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @see UserRepository#acceptFriendshipRequest(String, String)
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void cancelFriendshipRequest(String userIdFrom, String userIdTo);
    
    /**
     * Delete a friendship relation between two users.
     * Don't fail if friendship doesn't exist, it won't corrupt data.
     * @see UserRepository#revokeFriendship(String, String)
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    void revokeFriendship(String userIdFrom, String userIdTo);
}
