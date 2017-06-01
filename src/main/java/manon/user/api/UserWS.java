package manon.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.service.RegistrationService;
import manon.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_USER;
import static manon.app.config.API.API_V1;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/** User API. */
@RestController
@RequestMapping(value = API_V1 + API_USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserWS {
    
    private final UserService userService;
    private final RegistrationService registrationService;
    
    /** Get all users. */
    @RequestMapping(value = "/all", method = GET)
    public Page<User> findAll(@AuthenticationPrincipal UserSimpleDetails admin,
                              Pageable pageable)
            throws Exception {
        log.info("admin {} finds all users pageable {}", admin.getUsername(), pageable);
        return userService.findAll(pageable);
    }
    
    /** Activate a user. */
    @RequestMapping(value = "/{userId}/activate", method = POST)
    public String activate(@AuthenticationPrincipal UserSimpleDetails admin,
                           @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} activates user {}", admin.getUsername(), userId);
        return registrationService.activate(userId).getRegistrationState().name();
    }
    
    /** Suspend a user. */
    @RequestMapping(value = "/{userId}/suspend", method = POST)
    public String suspend(@AuthenticationPrincipal UserSimpleDetails admin,
                          @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} suspends user {}", admin.getUsername(), userId);
        return registrationService.suspend(userId).getRegistrationState().name();
    }
    
    /** Ban a user. */
    @RequestMapping(value = "/{userId}/ban", method = POST)
    public String ban(@AuthenticationPrincipal UserSimpleDetails admin,
                      @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} bans user {}", admin.getUsername(), userId);
        return registrationService.ban(userId).getRegistrationState().name();
    }
}
