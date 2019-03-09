package manon.service.user;

import manon.err.user.FriendshipExistsException;
import manon.err.user.FriendshipNotFoundException;
import manon.err.user.FriendshipRequestExistsException;
import manon.err.user.FriendshipRequestNotFoundException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserPublicInfo;

import java.util.List;

public interface FriendshipService {
    
    /**
     * Add a friendship request between two users.
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    void askFriendship(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipExistsException, FriendshipRequestExistsException;
    
    /**
     * Create a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void acceptFriendshipRequest(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipRequestNotFoundException;
    
    /**
     * Reject a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void rejectFriendshipRequest(long userIdFrom, long userIdTo) throws UserNotFoundException, FriendshipRequestNotFoundException;
    
    /**
     * Cancel a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void cancelFriendshipRequest(long userIdFrom, long userIdTo) throws UserNotFoundException, FriendshipRequestNotFoundException;
    
    /**
     * Delete a friendship relation between two users.
     * Don't fail if friendship doesn't exist, it won't corrupt data.
     * @param userIdFrom id of user who wants to delete friendship.
     * @param userIdTo if of friend user.
     */
    void revokeFriendship(long userIdFrom, long userIdTo) throws UserNotFoundException, FriendshipNotFoundException;
    
    /**
     * Get user's friends public information.
     * @param userId user id.
     * @return friends public information.
     */
    List<UserPublicInfo> findAllFor(long userId);
}
