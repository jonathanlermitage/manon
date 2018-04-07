package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.model.RegistrationState;
import manon.user.model.UserAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static manon.user.model.RegistrationState.ACTIVE;
import static manon.user.model.RegistrationState.BANNED;
import static manon.user.model.RegistrationState.DELETED;
import static manon.user.model.RegistrationState.SUSPENDED;
import static manon.user.model.UserAuthority.ROLE_ACTUATOR;
import static manon.user.model.UserAuthority.ROLE_ADMIN;
import static manon.user.model.UserAuthority.ROLE_DEV;
import static manon.user.model.UserAuthority.ROLE_PLAYER;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    
    private final UserService userService;
    
    @Value("${manon.admin.default-admin.username}")
    private String adminUsername;
    
    @Value("${manon.admin.default-admin.password}")
    private String adminPassword;
    
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
        return register(Arrays.asList(ROLE_ADMIN, ROLE_PLAYER, ROLE_ACTUATOR, ROLE_DEV), username, password, ACTIVE);
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
        return register(Collections.singletonList(role), username, password, registrationState);
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
    
    @Override
    public User ensureAdmin() throws UserExistsException {
        Optional<User> opAdmin = userService.findByUsername(adminUsername);
        if (opAdmin.isPresent()) {
            return opAdmin.get();
        }
        return registerRoot(adminUsername, adminPassword);
    }
}
