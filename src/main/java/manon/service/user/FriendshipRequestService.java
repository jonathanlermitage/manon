package manon.service.user;

import manon.document.user.FriendshipRequestEntity;
import manon.util.ExistForTesting;

import java.util.List;

public interface FriendshipRequestService {

    /**
     * Add a friendship request between two users.
     * @param userIdFrom id of user that asks for friendship.
     * @param userIdTo id of user that is targeted.
     */
    void askFriendship(long userIdFrom, long userIdTo);

    /**
     * Create a friendship request between two users.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void acceptFriendshipRequest(long userIdFrom, long userIdTo);

    /**
     * Reject a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that rejects friendship.
     */
    void rejectFriendshipRequest(long userIdFrom, long userIdTo);

    /**
     * Cancel a friendship request between two users.
     * Don't fail if friendship request doesn't exist, it won't corrupt data.
     * @param userIdFrom id of user that asked for friendship.
     * @param userIdTo id of user that accepts friendship.
     */
    void cancelFriendshipRequest(long userIdFrom, long userIdTo);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();

    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    long countFriendshipRequestCouple(long userId1, long userId2);

    /** Find all friendship requests from given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    List<FriendshipRequestEntity> findAllFriendshipRequestsByRequestFrom(long userId);

    /** Find all friendship requests to given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    List<FriendshipRequestEntity> findAllFriendshipRequestsByRequestTo(long userId);
}
