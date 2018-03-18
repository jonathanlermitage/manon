package manon.user.registration.service;

import lombok.RequiredArgsConstructor;
import manon.user.UserAuthority;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationState;
import manon.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static manon.user.UserAuthority.ROLE_ACTUATOR;
import static manon.user.UserAuthority.ROLE_ADMIN;
import static manon.user.UserAuthority.ROLE_DEV;
import static manon.user.UserAuthority.ROLE_PLAYER;
import static manon.user.registration.RegistrationState.ACTIVE;
import static manon.user.registration.RegistrationState.BANNED;
import static manon.user.registration.RegistrationState.DELETED;
import static manon.user.registration.RegistrationState.SUSPENDED;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    
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
    public void delete(String userId) {
        userService.setRegistrationState(userId, DELETED);
    }
    
    @Override
    public User registerPlayer(String username, String password) throws UserExistsException {
        return register(ROLE_PLAYER, username, password, ACTIVE);
    }
    
    @Override
    public User registerRoot(String username, String password) throws UserExistsException {
        return register(List.of(ROLE_ADMIN, ROLE_PLAYER, ROLE_ACTUATOR, ROLE_DEV), username, password, ACTIVE);
    }
    
    /**
     * Register a user.
     * @param role single role.
     * @param username username.
     * @param password password.
     * @param registrationState registration state.
     * @return user.
     */
    private User register(UserAuthority role, String username, String password, RegistrationState registrationState)
            throws UserExistsException {
        return register(List.of(role), username, password, registrationState);
    }
    
    /**
     * Register a user.
     * @param roles roles.
     * @param username username.
     * @param password password.
     * @param registrationState registration state.
     * @return user.
     */
    private User register(List<UserAuthority> roles, String username, String password, RegistrationState registrationState)
            throws UserExistsException {
        User user = User.builder()
                .username(username.trim())
                .roles(roles)
                .password(password)
                .registrationState(registrationState)
                .build();
        return userService.create(user);
    }
}
