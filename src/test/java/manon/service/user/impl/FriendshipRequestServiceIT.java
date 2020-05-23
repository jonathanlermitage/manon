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
        friendshipRequestService.askFriendship(userId(1), userId(2));

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(userId(1), userId(2))).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailAskFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(userId1, userId2))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldFailAskFriendshipWhenFriendshipRequestExists() {
        friendshipRequestService.askFriendship(userId(1), userId(2));

        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(userId(1), userId(2)))
            .isInstanceOf(FriendshipRequestExistsException.class);
    }

    @Test
    void shouldFailAskFriendshipWhenFriendshipExists() {
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.acceptFriendshipRequest(userId(1), userId(2));

        Assertions.assertThatThrownBy(() -> friendshipRequestService.askFriendship(userId(1), userId(2)))
            .isInstanceOf(FriendshipExistsException.class);
    }

    //
    // acceptFriendshipRequest
    //

    @Test
    void shouldAcceptFriendshipRequest() {
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.acceptFriendshipRequest(userId(1), userId(2));

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).hasSize(1);
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(userId(1), userId(2))).isEqualTo(0);
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
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.rejectFriendshipRequest(userId(1), userId(2));

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(userId(1), userId(2))).isEqualTo(0);
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
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.cancelFriendshipRequest(userId(1), userId(2));

        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(userId(1), userId(2))).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    void shouldFailCancelFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipRequestService.cancelFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }
}
