package manon.api.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.err.user.FriendshipExistsException;
import manon.err.user.FriendshipNotFoundException;
import manon.err.user.FriendshipRequestExistsException;
import manon.err.user.FriendshipRequestNotFoundException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserPublicInfo;
import manon.model.user.UserSimpleDetails;
import manon.service.user.FriendshipService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.Globals.API.API_USER;
import static manon.util.Tools.Media.JSON;

@Api(description = "Friendship operations between users. Used by: registered users.")
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class FriendshipWS {
    
    private final FriendshipService friendshipService;
    
    /** Create a friendship request to another user. */
    @ApiOperation(value = "Create a friendship request to another user.")
    @PostMapping("/askfriendship/user/{id}")
    public void askFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                              @PathVariable("id") long id)
        throws UserNotFoundException, FriendshipExistsException, FriendshipRequestExistsException {
        log.debug("user {} asks friendship request to user {}", user.getIdentity(), id);
        friendshipService.askFriendship(user.getUser().getId(), id);
    }
    
    /** Accept a friendship request from another user. */
    @ApiOperation(value = "Accept a friendship request from another user.")
    @PostMapping("/acceptfriendship/user/{id}")
    public void acceptFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        log.debug("user {} accepts friendship request from user {}", user.getIdentity(), id);
        friendshipService.acceptFriendshipRequest(id, user.getUser().getId());
    }
    
    /** Reject a friendship request from another user. */
    @ApiOperation(value = "Reject a friendship request from another user.")
    @PostMapping("/rejectfriendship/user/{id}")
    public void rejectFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        log.debug("user {} rejects friendship request from user {}", user.getIdentity(), id);
        friendshipService.rejectFriendshipRequest(id, user.getUser().getId());
    }
    
    /** Cancel a friendship request to another user. */
    @ApiOperation(value = "Cancel a friendship request to another user.")
    @PostMapping("/cancelfriendship/user/{id}")
    public void cancelFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        log.debug("user {} cancels friendship request to user {}", user.getIdentity(), id);
        friendshipService.cancelFriendshipRequest(user.getUser().getId(), id);
    }
    
    /** Delete an existing friendship relation with another user. */
    @ApiOperation(value = "Delete an existing friendship relation with another user.")
    @PostMapping("/revokefriendship/user/{id}")
    public void revokeFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                                 @PathVariable("id") long id)
        throws UserNotFoundException, FriendshipNotFoundException {
        log.debug("user {} deletes friendship with user {}", user.getIdentity(), id);
        friendshipService.revokeFriendship(user.getUser().getId(), id);
    }
    
    /** Get friends public information. */
    @ApiOperation(value = "Get friends public information. Returns complete list of friends.", produces = JSON, response = List.class)
    @GetMapping("/friends")
    public List<UserPublicInfo> getFriends(@AuthenticationPrincipal UserSimpleDetails user) {
        log.debug("user {} reads his friends", user.getIdentity());
        return friendshipService.findAllFor(user.getUserId());
    }
}
