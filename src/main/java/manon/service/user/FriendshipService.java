package manon.service.user;

import manon.document.user.Friendship;
import manon.err.user.FriendshipNotFoundException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserPublicInfo;
import manon.util.ExistForTesting;

import java.util.List;

public interface FriendshipService {
    
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
    List<UserPublicInfo> findAllPublicInfoFor(long userId);
    
    long countCouple(long userId1, long userId2);
    
    Friendship save(Friendship entity);
    
    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
    
    /** Find all friends for given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    List<Friendship> findAllFor(long userId);
}
