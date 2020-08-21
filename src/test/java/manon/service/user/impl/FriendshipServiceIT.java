package manon.service.user.impl;

import manon.err.user.FriendshipNotFoundException;
import manon.mapper.user.UserMapper;
import manon.util.basetest.AbstractIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FriendshipServiceIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 3;
    }

    @Override
    public void beforeEachTestOnceDbPopulated() {
        super.beforeEachTestOnceDbPopulated();
        uid1 = userId(1);
        uid2 = userId(2);
    }

    long uid1;
    long uid2;

    Object[] dataProviderUnknownUserInCouple() {
        return new Object[][]{
            {UNKNOWN_ID, uid2},
            {uid2, UNKNOWN_ID},
            {UNKNOWN_ID, UNKNOWN_ID}
        };
    }

    //
    // revokeFriendship
    //

    @Test
    void shouldRevokeFriendship() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.acceptFriendshipRequest(uid1, uid2);
        friendshipService.revokeFriendship(uid1, uid2);

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(uid1, uid2)).isZero();
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailRevokeFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.revokeFriendship(userId1, userId2))
            .isInstanceOf(FriendshipNotFoundException.class);
    }

    //
    // findAllPublicInfoFor
    //

    @Test
    void shouldFindAllForWhenUserHasFriends() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.askFriendship(uid1, userId(3));
        friendshipRequestService.acceptFriendshipRequest(uid1, uid2);
        friendshipRequestService.acceptFriendshipRequest(uid1, userId(3));

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).containsExactlyInAnyOrder(
            UserMapper.MAPPER.toUserPublicInfo(user(2)), UserMapper.MAPPER.toUserPublicInfo(user(3))
        );
    }

    @Test
    void shouldFindAllForWhenUserHasNoFriend() {
        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).isEmpty();
    }
}
