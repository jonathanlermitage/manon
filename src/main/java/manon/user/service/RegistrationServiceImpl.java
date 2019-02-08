package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.app.config.Cfg;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
    
    private final Cfg cfg;
    private final UserService userService;
    
    @Override
    public User activate(long userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, ACTIVE);
        return userService.readOne(userId);
    }
    
    @Override
    public User ban(long userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, BANNED);
        return userService.readOne(userId);
    }
    
    @Override
    public User suspend(long userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, SUSPENDED);
        return userService.readOne(userId);
    }
    
    @Override
    public User delete(long userId) throws UserNotFoundException {
        userService.setRegistrationState(userId, DELETED);
        return userService.readOne(userId);
    }
    
    @Override
    public User registerPlayer(String username, String password) throws UserExistsException {
        User user = User.builder()
            .username(username.trim())
            .authorities(ROLE_PLAYER.name())
            .password(password)
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }
    
    @Override
    public User ensureAdmin() throws UserExistsException {
        Optional<User> opAdmin = userService.findByUsername(cfg.getAdminDefaultAdminUsername());
        if (opAdmin.isPresent()) {
            return opAdmin.get();
        }
        User user = User.builder()
            .username(cfg.getAdminDefaultAdminUsername().trim())
            .authorities(Stream.of(ROLE_ADMIN, ROLE_PLAYER, ROLE_ACTUATOR, ROLE_DEV).map(Enum::name).collect(Collectors.joining(",")))
            .password(cfg.getAdminDefaultAdminPassword())
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }
}
