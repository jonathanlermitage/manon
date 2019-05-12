package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import manon.app.Cfg;
import manon.document.user.User;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.model.user.UserRole;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static manon.model.user.RegistrationState.ACTIVE;
import static manon.model.user.RegistrationState.BANNED;
import static manon.model.user.RegistrationState.DELETED;
import static manon.model.user.RegistrationState.SUSPENDED;
import static manon.model.user.UserRole.ACTUATOR;
import static manon.model.user.UserRole.PLAYER;

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
            .authorities(PLAYER.getAuthority())
            .password(password)
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }
    
    @Override
    public User ensureActuator() {
        return ensureUser(cfg.getDefaultUserActuatorUsername(), cfg.getDefaultUserActuatorPassword(), ACTUATOR, PLAYER);
    }
    
    @Override
    public User ensureAdmin() {
        return ensureUser(cfg.getDefaultUserAdminUsername(), cfg.getDefaultUserAdminPassword(), UserRole.values());
    }
    
    @SneakyThrows(UserExistsException.class)
    public User ensureUser(String username, String password, UserRole... roles) {
        Optional<User> existingUser = userService.findByUsername(username);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        User user = User.builder()
            .username(username)
            .authorities(Stream.of(roles).map(UserRole::getAuthority).collect(Collectors.joining(",")))
            .password(password)
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }
}
