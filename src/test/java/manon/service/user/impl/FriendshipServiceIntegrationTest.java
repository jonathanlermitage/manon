package manon.service.user.impl;

import manon.err.user.FriendshipNotFoundException;
import manon.model.user.UserPublicInfo;
import manon.util.basetest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FriendshipServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Override
    public int getNumberOfUsers() {
        return 3;
    }
    
    public Object[] dataProviderUnknownUserInCouple() {
        return new Object[][]{
            {UNKNOWN_ID, userId(2)},
            {userId(2), UNKNOWN_ID},
            {UNKNOWN_ID, UNKNOWN_ID}
        };
    }
    
    //
    // revokeFriendship
    //
    
    @Test
    public void shouldRevokeFriendship() throws Exception {
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.acceptFriendshipRequest(userId(1), userId(2));
        friendshipService.revokeFriendship(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestService.countFriendshipRequestCouple(userId(1), userId(2))).isEqualTo(0);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailRevokeFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.revokeFriendship(userId1, userId2))
            .isInstanceOf(FriendshipNotFoundException.class);
    }
    
    //
    // findAllPublicInfoFor
    //
    
    @Test
    public void shouldFindAllForWhenUserHasFriends() throws Exception {
        friendshipRequestService.askFriendship(userId(1), userId(2));
        friendshipRequestService.askFriendship(userId(1), userId(3));
        friendshipRequestService.acceptFriendshipRequest(userId(1), userId(2));
        friendshipRequestService.acceptFriendshipRequest(userId(1), userId(3));
        
        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).containsExactlyInAnyOrder(
            UserPublicInfo.from(user(2)), UserPublicInfo.from(user(3))
        );
    }
    
    @Test
    public void shouldFindAllForWhenUserHasNoFriend() {
        Assertions.assertThat(friendshipService.findAllPublicInfoFor(userId(1))).isEmpty();
    }
}
