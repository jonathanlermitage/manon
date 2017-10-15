package manon.profile.friendship.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.profile.ProfileNotFoundException;
import manon.profile.friendship.FriendshipExistsException;
import manon.profile.friendship.FriendshipRequestExistsException;
import manon.profile.friendship.FriendshipRequestNotFoundException;
import manon.profile.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_PROFILE;
import static manon.app.config.API.API_V1;

@RestController
@RequestMapping(value = API_V1 + API_PROFILE)
@RequiredArgsConstructor
@Slf4j
public class FriendshipWS {
    
    private final ProfileService profileService;
    
    /** Create a friendship request to another profile. */
    @PostMapping(value = "/askfriendship/profile/{id}")
    public void askFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                              @PathVariable("id") String id)
            throws ProfileNotFoundException, FriendshipExistsException, FriendshipRequestExistsException {
        log.info("user {} asks friendship request to profile {}", user.getIdentity(), id);
        profileService.askFriendship(user.getUser().getProfileId(), id);
    }
    
    /** Accept a friendship request from another profile. */
    @PostMapping(value = "/acceptfriendship/profile/{id}")
    public void acceptFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") String id)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException {
        log.info("user {} accepts friendship request from profile {}", user.getIdentity(), id);
        profileService.acceptFriendshipRequest(id, user.getUser().getProfileId());
    }
    
    /** Reject a friendship request from another profile. */
    @PostMapping(value = "/rejectfriendship/profile/{id}")
    public void rejectFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") String id)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException {
        log.info("user {} rejects friendship request from profile {}", user.getIdentity(), id);
        profileService.rejectFriendshipRequest(id, user.getUser().getProfileId());
    }
    
    /** Cancel a friendship request to another profile. */
    @PostMapping(value = "/cancelfriendship/profile/{id}")
    public void cancelFriendshipRequest(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("id") String id)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException {
        log.info("user {} cancels friendship request to profile {}", user.getIdentity(), id);
        profileService.cancelFriendshipRequest(user.getUser().getProfileId(), id);
    }
    
    /** Delete an existing friendship relation with another profile. */
    @PostMapping(value = "/revokefriendship/profile/{id}")
    public void revokeFriendship(@AuthenticationPrincipal UserSimpleDetails user,
                                 @PathVariable("id") String id)
            throws ProfileNotFoundException, FriendshipRequestNotFoundException {
        log.info("user {} deletes friendship with profile {}", user.getIdentity(), id);
        profileService.revokeFriendship(user.getUser().getProfileId(), id);
    }
}
