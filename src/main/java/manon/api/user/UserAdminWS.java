package manon.api.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.document.DefaultView;
import manon.document.user.User;
import manon.model.user.UserSimpleDetails;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                           @PathVariable("userId") long userId) {
        log.warn("admin {} activates user {}", admin.getUsername(), userId);
        return registrationService.activate(userId).getRegistrationState().name();
    }
    
    /** Suspend a user. */
    @ApiOperation(value = "Suspend a user. Returns registration state name.", produces = TEXT)
    @PostMapping(value = "/{userId}/suspend")
    public String suspend(@AuthenticationPrincipal UserSimpleDetails admin,
                          @PathVariable("userId") long userId) {
        log.warn("admin {} suspends user {}", admin.getUsername(), userId);
        return registrationService.suspend(userId).getRegistrationState().name();
    }
    
    /** Ban a user. */
    @ApiOperation(value = "Ban a user. Returns registration state name.", produces = TEXT)
    @PostMapping(value = "/{userId}/ban")
    public String ban(@AuthenticationPrincipal UserSimpleDetails admin,
                      @PathVariable("userId") long userId) {
        log.warn("admin {} bans user {}", admin.getUsername(), userId);
        return registrationService.ban(userId).getRegistrationState().name();
    }
    
    @ApiOperation(value = "Search users via Querydsl.", produces = JSON, response = Page.class)
    @PostMapping(value = "/search")
    @JsonView(DefaultView.class)
    public Page<User> search(@AuthenticationPrincipal UserSimpleDetails admin,
                             @QuerydslPredicate(root = User.class) Predicate predicate,
                             Pageable pageable) {
        log.debug("admin {} uses Querydsl to search users with predicate {}, page {}",
            admin.getUsername(), predicate, pageable);
        return userService.search(predicate, pageable);
    }
    
    @ApiOperation(value = "Search users via username, nickname or email.", produces = JSON, response = Page.class)
    @PostMapping(value = "/search/identity")
    @JsonView(DefaultView.class)
    public Page<User> searchByIdentity(@AuthenticationPrincipal UserSimpleDetails admin,
                                       @RequestParam(name = "username", required = false) String username,
                                       @RequestParam(name = "nickname", required = false) String nickname,
                                       @RequestParam(name = "email", required = false) String email,
                                       Pageable pageable) {
        log.debug("admin {} uses Querydsl to search users with username {}, nickname {}, email {}, page {}",
            admin.getUsername(), username, nickname, email, pageable);
        return userService.searchByIdentity(username, nickname, email, pageable);
    }
}
