package manon.api.user;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.document.DefaultView;
import manon.document.user.User;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserSimpleDetails;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_USER_ADMIN;
import static manon.util.Tools.Media.JSON;
import static manon.util.Tools.Media.TEXT;

/** User admin API. */
@Api(description = "User administration tasks. Used by: admin.")
@RestController
@RequestMapping(value = API_USER_ADMIN)
@RequiredArgsConstructor
@Slf4j
public class UserAdminWS {
    
    private final RegistrationService registrationService;
    private final UserService userService;
    
    /** Get all users. */
    @ApiOperation(value = "Get all users. Result is paginated.", produces = JSON, response = Page.class)
    @GetMapping(value = "/all")
    @JsonView(DefaultView.class)
    public Page<User> findAll(@AuthenticationPrincipal UserSimpleDetails admin,
                              Pageable pageable) {
        log.debug("admin {} finds all users pageable {}", admin.getUsername(), pageable);
        return userService.findAll(pageable);
    }
    
    /** Activate a user. */
    @ApiOperation(value = "Activate a user. Returns registration state name.", produces = TEXT)
    @PostMapping(value = "/{userId}/activate")
    public String activate(@AuthenticationPrincipal UserSimpleDetails admin,
                           @PathVariable("userId") long userId)
        throws UserNotFoundException {
        log.warn("admin {} activates user {}", admin.getUsername(), userId);
        return registrationService.activate(userId).getRegistrationState().name();
    }
    
    /** Suspend a user. */
    @ApiOperation(value = "Suspend a user. Returns registration state name.", produces = TEXT)
    @PostMapping(value = "/{userId}/suspend")
    public String suspend(@AuthenticationPrincipal UserSimpleDetails admin,
                          @PathVariable("userId") long userId)
        throws UserNotFoundException {
        log.warn("admin {} suspends user {}", admin.getUsername(), userId);
        return registrationService.suspend(userId).getRegistrationState().name();
    }
    
    /** Ban a user. */
    @ApiOperation(value = "Ban a user. Returns registration state name.", produces = TEXT)
    @PostMapping(value = "/{userId}/ban")
    public String ban(@AuthenticationPrincipal UserSimpleDetails admin,
                      @PathVariable("userId") long userId)
        throws UserNotFoundException {
        log.warn("admin {} bans user {}", admin.getUsername(), userId);
        return registrationService.ban(userId).getRegistrationState().name();
    }
}
