package manon.service.user.impl;

import manon.err.user.FriendshipExistsException;
import manon.err.user.FriendshipNotFoundException;
import manon.err.user.FriendshipRequestExistsException;
import manon.err.user.FriendshipRequestNotFoundException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserPublicInfo;
import manon.service.user.FriendshipService;
import manon.util.basetest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

public class FriendshipServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private FriendshipService friendshipService;
    
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
    // askFriendship
    //
    
    @Test
    public void shouldAskFriendship() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestRepository.countCouple(userId(1), userId(2))).isEqualTo(1);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailAskFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.askFriendship(userId1, userId2))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldFailAskFriendshipWhenFriendshipRequestExists() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        
        Assertions.assertThatThrownBy(() -> friendshipService.askFriendship(userId(1), userId(2)))
            .isInstanceOf(FriendshipRequestExistsException.class);
    }
    
    @Test
    public void shouldFailAskFriendshipWhenFriendshipExists() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.acceptFriendshipRequest(userId(1), userId(2));
        
        Assertions.assertThatThrownBy(() -> friendshipService.askFriendship(userId(1), userId(2)))
            .isInstanceOf(FriendshipExistsException.class);
    }
    
    //
    // acceptFriendshipRequest
    //
    
    @Test
    public void shouldAcceptFriendshipRequest() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.acceptFriendshipRequest(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).hasSize(1);
        Assertions.assertThat(friendshipRequestRepository.countCouple(userId(1), userId(2))).isEqualTo(0);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailAcceptFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.acceptFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }
    
    //
    // rejectFriendshipRequest
    //
    
    @Test
    public void shouldRejectFriendshipRequest() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.rejectFriendshipRequest(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestRepository.countCouple(userId(1), userId(2))).isEqualTo(0);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailRejectFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.rejectFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }
    
    //
    // cancelFriendshipRequest
    //
    
    @Test
    public void shouldCancelFriendshipRequest() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.cancelFriendshipRequest(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestRepository.countCouple(userId(1), userId(2))).isEqualTo(0);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailCancelFriendshipRequestWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.cancelFriendshipRequest(userId1, userId2))
            .isInstanceOf(FriendshipRequestNotFoundException.class);
    }
    
    //
    // revokeFriendship
    //
    
    @Test
    public void shouldRevokeFriendship() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.acceptFriendshipRequest(userId(1), userId(2));
        friendshipService.revokeFriendship(userId(1), userId(2));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).isEmpty();
        Assertions.assertThat(friendshipRequestRepository.countCouple(userId(1), userId(2))).isEqualTo(0);
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderUnknownUserInCouple")
    public void shouldFailRevokeFriendshipWhenUserNotFound(long userId1, long userId2) {
        Assertions.assertThatThrownBy(() -> friendshipService.revokeFriendship(userId1, userId2))
            .isInstanceOf(FriendshipNotFoundException.class);
    }
    
    //
    // findAllFor
    //
    
    @Test
    public void shouldFindAllForWhenUserHasFriends() throws Exception {
        friendshipService.askFriendship(userId(1), userId(2));
        friendshipService.askFriendship(userId(1), userId(3));
        friendshipService.acceptFriendshipRequest(userId(1), userId(2));
        friendshipService.acceptFriendshipRequest(userId(1), userId(3));
        
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).containsExactlyInAnyOrder(
            UserPublicInfo.from(user(2)), UserPublicInfo.from(user(3))
        );
    }
    
    @Test
    public void shouldFindAllForWhenUserHasNoFriend() {
        Assertions.assertThat(friendshipService.findAllFor(userId(1))).isEmpty();
    }
}
