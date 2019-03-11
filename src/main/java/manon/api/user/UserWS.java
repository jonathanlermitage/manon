package manon.api.user;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.document.DefaultView;
import manon.document.user.User;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserSimpleDetails;
import manon.model.user.form.RegistrationForm;
import manon.model.user.form.UserPasswordUpdateForm;
import manon.model.user.form.UserUpdateForm;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_USER;
import static manon.util.Tools.Media.JSON;
import static org.springframework.http.HttpStatus.CREATED;

/** User API. */
@Api(description = "Register new user and manipulate own user data. Used by: registered users, except registration that is public.")
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class UserWS {
    
    private final RegistrationService registrationService;
    private final UserService userService;
    
    /** Register a new user. */
    @ApiOperation(value = "Register and return a new user. This endpoint is public.", consumes = JSON, produces = JSON, response = User.class)
    @PostMapping(consumes = JSON)
    @ResponseStatus(CREATED)
    @JsonView(DefaultView.class)
    public User register(@RequestBody @Validated RegistrationForm registrationForm)
        throws UserExistsException {
        log.debug("user registration with {}", registrationForm);
        return registrationService.registerPlayer(registrationForm.getUsername(), registrationForm.getPassword());
    }
    
    /** Unregister a user. */
    @ApiOperation(value = "Unregister me.")
    @DeleteMapping
    public void delete(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} deletes himself", user.getIdentity());
        registrationService.delete(user.getUserId());
    }
    
    /** Get user. */
    @ApiOperation(value = "Get my user information.", produces = JSON, response = User.class)
    @GetMapping
    @JsonView(DefaultView.class)
    public User read(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} reads his user", user.getIdentity());
        return userService.readOne(user.getUserId());
    }
    
    /** Get user and linked user snapshots. */
    @ApiOperation(value = "Get my user information and linked user snapshots.", produces = JSON, response = User.class)
    @GetMapping("/include/usersnapshots")
    @JsonView(User.WithUserSnapshots.class)
    public User readAndIncludeUserSnapshots(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} reads his user", user.getIdentity());
        return userService.readOneAndFetchUserSnapshots(user.getUserId()); // todo jouer des snapshots puis tester le WS dans firefox
    }
    
    /** Get user's version. */
    @ApiOperation(value = "Get my user version number.", produces = JSON, response = Long.class)
    @GetMapping("/version")
    public long readVersion(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} reads his version", user.getIdentity());
        return userService.readVersionById(user.getUserId()).getVersion();
    }
    
    /** Update one user's user field. */
    @ApiOperation(value = "Update my user data.", consumes = JSON)
    @PutMapping(value = "/field", consumes = JSON)
    public void update(@AuthenticationPrincipal UserSimpleDetails user,
                       @RequestBody @Validated UserUpdateForm userUpdateForm) {
        log.debug("user {} updates his user with {}", user.getIdentity(), userUpdateForm);
        userService.update(user.getUserId(), userUpdateForm);
    }
    
    /** Update current user's password. */
    @ApiOperation(value = "Update my user password.", consumes = JSON)
    @PutMapping(value = "/password", consumes = JSON)
    public void updatePassword(@AuthenticationPrincipal UserSimpleDetails user,
                               @RequestBody @Validated UserPasswordUpdateForm userPasswordUpdateForm)
        throws PasswordNotMatchException {
        log.debug("user {} updates his password", user.getIdentity());
        userService.validatePassword(userPasswordUpdateForm.getOldPassword(), user.getUser().getPassword());
        userService.encodeAndSetPassword(user.getUserId(), userPasswordUpdateForm.getNewPassword());
    }
}
