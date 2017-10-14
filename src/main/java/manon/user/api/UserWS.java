package manon.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.service.RegistrationService;
import manon.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_USER;
import static manon.app.config.API.API_V1;

/** User API. */
@RestController
@RequestMapping(value = API_V1 + API_USER)
@RequiredArgsConstructor
@Slf4j
public class UserWS {
    
    private final UserService userService;
    private final RegistrationService registrationService;
    
    /** Get all users. */
    @RequestMapping(value = "/all")
    public Page<User> findAll(@AuthenticationPrincipal UserSimpleDetails admin,
                              Pageable pageable)
            throws Exception {
        log.info("admin {} finds all users pageable {}", admin.getUsername(), pageable);
        return userService.findAll(pageable);
    }
    
    /** Activate a user. */
    @PostMapping(value = "/{userId}/activate")
    public String activate(@AuthenticationPrincipal UserSimpleDetails admin,
                           @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} activates user {}", admin.getUsername(), userId);
        return registrationService.activate(userId).getRegistrationState().name();
    }
    
    /** Suspend a user. */
    @PostMapping(value = "/{userId}/suspend")
    public String suspend(@AuthenticationPrincipal UserSimpleDetails admin,
                          @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} suspends user {}", admin.getUsername(), userId);
        return registrationService.suspend(userId).getRegistrationState().name();
    }
    
    /** Ban a user. */
    @PostMapping(value = "/{userId}/ban")
    public String ban(@AuthenticationPrincipal UserSimpleDetails admin,
                      @PathVariable("userId") String userId)
            throws UserNotFoundException {
        log.warn("admin {} bans user {}", admin.getUsername(), userId);
        return registrationService.ban(userId).getRegistrationState().name();
    }
}
