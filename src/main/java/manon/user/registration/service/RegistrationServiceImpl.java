package manon.user.registration.service;

import lombok.RequiredArgsConstructor;
import manon.app.security.PasswordEncoderService;
import manon.user.UserAuthority;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import manon.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static manon.user.UserAuthority.ADMIN;
import static manon.user.UserAuthority.PLAYER;
import static manon.user.registration.RegistrationStateEnum.ACTIVE;
import static manon.user.registration.RegistrationStateEnum.BANNED;
import static manon.user.registration.RegistrationStateEnum.DELETED;
import static manon.user.registration.RegistrationStateEnum.SUSPENDED;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    
    private final PasswordEncoderService passwordEncoderService;
    private final UserService userService;
    
    @Override
    public User activate(String userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, ACTIVE);
        return userService.readOne(userId);
    }
    
    @Override
    public User ban(String userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, BANNED);
        return userService.readOne(userId);
    }
    
    @Override
    public User suspend(String userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, SUSPENDED);
        return userService.readOne(userId);
    }
    
    @Override
    public void delete(String userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, DELETED);
    }
    
    @Override
    public User registerPlayer(String username, String password)
            throws UserExistsException {
        return register(PLAYER, username, password, ACTIVE);
    }
    
    @Override
    public User registerRoot(String username, String password)
            throws UserExistsException {
        return register(Arrays.asList(ADMIN, PLAYER), username, password, ACTIVE);
    }
    
    /**
     * Register a user.
     * @param role single role.
     * @param username username.
     * @param password password.
     * @param registrationState registration state.
     * @return user.
     */
    private User register(UserAuthority role, String username, String password, RegistrationStateEnum registrationState)
            throws UserExistsException {
        return register(singletonList(role), username, password, registrationState);
    }
    
    /**
     * Register a user.
     * @param roles roles.
     * @param username username.
     * @param password password.
     * @param registrationState registration state.
     * @return user.
     */
    private User register(List<UserAuthority> roles, String username, String password, RegistrationStateEnum registrationState)
            throws UserExistsException {
        User user = User.builder()
                .username(username.trim())
                .roles(roles)
                .password(passwordEncoderService.encode(password))
                .registrationState(registrationState)
                .build();
        return userService.create(user);
    }
}
