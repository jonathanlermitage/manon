package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
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
        return register(Collections.singletonList(ROLE_PLAYER), username, password);
    }
    
    @Override
    public User ensureAdmin() throws UserExistsException {
        Optional<User> opAdmin = userService.findByUsername(adminUsername);
        if (opAdmin.isPresent()) {
            return opAdmin.get();
        }
        return register(Arrays.asList(ROLE_ADMIN, ROLE_PLAYER, ROLE_ACTUATOR, ROLE_DEV), adminUsername, adminPassword);
    }
    
    /**
     * Register a user.
     * @param roles roles.
     * @param username username.
     * @param password password.
     * @return user.
     */
    private User register(List<UserAuthority> roles, String username, String password)
            throws UserExistsException {
        User user = User.builder()
                .username(username.trim())
                .roles(roles)
                .password(password)
                .registrationState(ACTIVE)
                .build();
        return userService.create(user);
    }
}
