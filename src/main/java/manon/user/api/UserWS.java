package manon.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.user.document.User;
import manon.user.err.PasswordNotMatchException;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.form.RegistrationForm;
import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserUpdateForm;
import manon.user.service.RegistrationService;
import manon.user.service.UserService;
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

import static manon.app.config.Globals.API.API_USER;
import static manon.util.Tools.MEDIA_JSON;
import static org.springframework.http.HttpStatus.CREATED;

/** User API. */
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class UserWS {
    
    private final RegistrationService registrationService;
    private final UserService userService;
    
    /** Register a new user. */
    @PostMapping(consumes = MEDIA_JSON)
    @ResponseStatus(CREATED)
    public User register(@RequestBody @Validated RegistrationForm registrationForm)
        throws UserExistsException {
        log.debug("user registration with {}", registrationForm);
        return registrationService.registerPlayer(registrationForm.getUsername(), registrationForm.getPassword());
    }
    
    /** Unregister a user. */
    @DeleteMapping
    public void delete(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} deletes himself", user.getIdentity());
        registrationService.delete(user.getUserId());
    }
    
    /** Get user. */
    @GetMapping
    public User read(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} reads his user", user.getIdentity());
        return userService.readOne(user.getUserId());
    }
    
    /** Get user's version. */
    @GetMapping("/version")
    public long readVersion(@AuthenticationPrincipal UserSimpleDetails user)
        throws UserNotFoundException {
        log.debug("user {} reads his version", user.getIdentity());
        return userService.readVersionById(user.getUserId()).getVersion();
    }
    
    /** Update one user's user field. */
    @PutMapping(value = "/field", consumes = MEDIA_JSON)
    public void update(@AuthenticationPrincipal UserSimpleDetails user,
                       @RequestBody @Validated UserUpdateForm userUpdateForm) {
        log.debug("user {} updates his user with {}", user.getIdentity(), userUpdateForm);
        userService.update(user.getUserId(), userUpdateForm);
    }
    
    /** Update current user's password. */
    @PutMapping(value = "/password", consumes = MEDIA_JSON)
    public void updatePassword(@AuthenticationPrincipal UserSimpleDetails user,
                               @RequestBody @Validated UserPasswordUpdateForm userPasswordUpdateForm)
        throws PasswordNotMatchException {
        log.debug("user {} updates his password", user.getIdentity());
        userService.validatePassword(userPasswordUpdateForm.getOldPassword(), user.getUser().getPassword());
        userService.encodeAndSetPassword(user.getUserId(), userPasswordUpdateForm.getNewPassword());
    }
}
