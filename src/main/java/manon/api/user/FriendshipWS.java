package manon.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.document.user.FriendshipRequestEntity;
import manon.dto.user.FriendshipRequestDto;
import manon.mapper.user.FriendshipMapper;
import manon.model.user.UserPublicInfo;
import manon.model.user.UserSimpleDetails;
import manon.service.user.FriendshipRequestService;
import manon.service.user.FriendshipService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.Globals.API.API_USER;

@Tag(name = "Friendship operations between users. Used by: registered users.")
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class FriendshipWS {

    private final FriendshipService friendshipService;
    private final FriendshipRequestService friendshipRequestService;

    /** Create a friendship request to another user. */
    @Operation(summary = "Create a friendship request to another user.")
    @PostMapping("/askfriendship/user/{id}")
    public void askFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                              @PathVariable("id") long id) {
        log.debug("user {} asks friendship request to user {}", user.getIdentity(), id);
        friendshipRequestService.askFriendship(user.getUser().getId(), id);
    }

    /** Accept a friendship request from another user. */
    @Operation(summary = "Accept a friendship request from another user.")
    @PostMapping("/acceptfriendship/user/{id}")
    public void acceptFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id) {
        log.debug("user {} accepts friendship request from user {}", user.getIdentity(), id);
        friendshipRequestService.acceptFriendshipRequest(id, user.getUser().getId());
    }

    /** Reject a friendship request from another user. */
    @Operation(summary = "Reject a friendship request from another user.")
    @PostMapping("/rejectfriendship/user/{id}")
    public void rejectFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id) {
        log.debug("user {} rejects friendship request from user {}", user.getIdentity(), id);
        friendshipRequestService.rejectFriendshipRequest(id, user.getUser().getId());
    }

    /** Cancel a friendship request to another user. */
    @Operation(summary = "Cancel a friendship request to another user.")
    @PostMapping("/cancelfriendship/user/{id}")
    public void cancelFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") long id) {
        log.debug("user {} cancels friendship request to user {}", user.getIdentity(), id);
        friendshipRequestService.cancelFriendshipRequest(user.getUser().getId(), id);
    }

    /** Delete an existing friendship relation with another user. */
    @Operation(summary = "Delete an existing friendship relation with another user.")
    @PostMapping("/revokefriendship/user/{id}")
    public void revokeFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                                 @PathVariable("id") long id) {
        log.debug("user {} deletes friendship with user {}", user.getIdentity(), id);
        friendshipService.revokeFriendship(user.getUser().getId(), id);
    }

    /** Get friends public information. */
    @Operation(summary = "Get friends public information. Returns complete list of friends.")
    @GetMapping("/friends")
    public List<UserPublicInfo> getFriends(@AuthenticationPrincipal UserSimpleDetails user) {
        log.debug("user {} reads his friends", user.getIdentity());
        return friendshipService.findAllPublicInfoFor(user.getUserId());
    }

    /** Get friendship requests. */
    @Operation(summary = "Get friendship requests.")
    @GetMapping("/friendshiprequests")
    public List<FriendshipRequestDto> getFriendshipRequests(@AuthenticationPrincipal UserSimpleDetails user) {
        log.debug("user {} reads his friendship requests", user.getIdentity());
        List<FriendshipRequestEntity> requests = friendshipRequestService.findAllFriendshipRequestsByRequestFromOrTo(user.getUserId());
        return FriendshipMapper.MAPPER.toFriendshipRequestDto(requests);
    }
}
