package manon.service.user.impl;

import manon.err.user.FriendshipExistsException;
import manon.err.user.FriendshipRequestExistsException;
import manon.err.user.FriendshipRequestNotFoundException;
import manon.err.user.UserNotFoundException;
import manon.util.basetest.AbstractIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FriendshipRequestServiceIT extends AbstractIT {

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
            {UNKNOWN_ID, userId(2)},
            {userId(2), UNKNOWN_ID},
            {UNKNOWN_ID, UNKNOWN_ID}
        };
    }

    //
    // askFriendship
    //

    @Test
    void shouldAskFriendship() {
        friendshipRequestService.askFriendship(uid1, uid2);

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(uid1, uid2)).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailAskFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(userId1, userId2))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldFailAskFriendshipWhenFriendshipRequestExists() {
        friendshipRequestService.askFriendship(uid1, uid2);

        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(uid1, uid2))
            .isInstanceOf(FriendshipRequestExistsException.class);
    }

    @Test
    void shouldFailAskFriendshipWhenFriendshipExists() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.acceptFriendshipRequest(uid1, uid2);

        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(uid1, uid2))
            .isInstanceOf(FriendshipExistsException.class);
    }

    //
    // acceptFriendshipRequest
    //

    @Test
    void shouldAcceptFriendshipRequest() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.acceptFriendshipRequest(uid1, uid2);

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).hasSize(1);
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(uid1, uid2)).isZero();
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailAcceptFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.acceptFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }

    //
    // rejectFriendshipRequest
    //

    @Test
    void shouldRejectFriendshipRequest() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.rejectFriendshipRequest(uid1, uid2);

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(uid1, uid1)).isZero();
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailRejectFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.rejectFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }

    //
    // cancelFriendshipRequest
    //

    @Test
    void shouldCancelFriendshipRequest() {
        friendshipRequestService.askFriendship(uid1, uid2);
        friendshipRequestService.cancelFriendshipRequest(uid1, uid2);

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(uid1)).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(uid1, uid1)).isZero();
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailCancelFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.cancelFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }
}
