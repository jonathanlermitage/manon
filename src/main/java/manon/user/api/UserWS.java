package manon.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.PasswordEncoderService;
import manon.app.security.UserSimpleDetails;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserPasswordUpdateFormException;
import manon.user.form.UserUpdateForm;
import manon.user.form.UserUpdateFormException;
import manon.user.registration.form.RegistrationForm;
import manon.user.registration.form.RegistrationFormException;
import manon.user.registration.service.RegistrationService;
import manon.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_USER;
import static manon.util.Tools.MEDIA_JSON;
import static manon.util.web.ValidationUtils.validate;
import static org.springframework.http.HttpStatus.CREATED;

/** User API. */
@RestController
@RequestMapping(value = API_USER)
@RequiredArgsConstructor
@Slf4j
public class UserWS {
    
    private final RegistrationService registrationService;
    private final UserService userService;
    private final PasswordEncoderService passwordEncoderService;
    
    /** Register a new user. */
    @PostMapping(consumes = MEDIA_JSON)
    @ResponseStatus(CREATED)
    public User register(@RequestBody RegistrationForm registrationForm, BindingResult bindingResult)
            throws UserExistsException, RegistrationFormException {
        log.info("user registration with {}", registrationForm);
        validate(registrationForm, bindingResult);
        return registrationService.registerPlayer(registrationForm.getUsername(), registrationForm.getPassword());
    }
    
    /** Unregister a user. */
    @DeleteMapping
    public void delete(@AuthenticationPrincipal UserSimpleDetails user)
            throws UserNotFoundException {
        log.info("user {} deletes himself", user.getIdentity());
        registrationService.delete(user.getUserId());
    }
    
    /** Get user's user. */
    @GetMapping
    public User read(@AuthenticationPrincipal UserSimpleDetails user)
            throws UserNotFoundException {
        log.info("user {} reads his user", user.getIdentity());
        return userService.readOne(user.getUserId());
    }
    
    /** Update one user's user field. */
    @PutMapping(value = "/field", consumes = MEDIA_JSON)
    public void updateField(@AuthenticationPrincipal UserSimpleDetails user,
                            @RequestBody UserUpdateForm userUpdateForm,
                            BindingResult bindingResult)
            throws UserNotFoundException, UserUpdateFormException {
        log.info("user {} updates his user with {}", user.getIdentity(), userUpdateForm);
        validate(userUpdateForm, bindingResult);
        userService.update(user.getUserId(), userUpdateForm);
    }
    
    /** Update current user's password. */
    @PutMapping(value = "/password", consumes = MEDIA_JSON)
    public void updatePassword(@AuthenticationPrincipal UserSimpleDetails user,
                               @RequestBody UserPasswordUpdateForm userPasswordUpdateForm,
                               BindingResult bindingResult)
            throws UserPasswordUpdateFormException, UserNotFoundException {
        log.info("user {} updates his password", user.getIdentity());
        validate(userPasswordUpdateForm, bindingResult);
        userService.setPassword(user.getUserId(), passwordEncoderService.encode(userPasswordUpdateForm.getNewPassword()));
    }
}
