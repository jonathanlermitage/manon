package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.document.user.User;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
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
import static manon.model.user.UserAuthority.ROLE_ACTUATOR;
import static manon.model.user.UserAuthority.ROLE_ADMIN;
import static manon.model.user.UserAuthority.ROLE_DEV;
import static manon.model.user.UserAuthority.ROLE_PLAYER;

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
