package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.app.Cfg;
import manon.document.user.UserEntity;
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
    public UserEntity activate(long userId) {
        userService.setRegistrationState(userId, ACTIVE);
        return userService.readOne(userId);
    }

    @Override
    public UserEntity ban(long userId) {
        userService.setRegistrationState(userId, BANNED);
        return userService.readOne(userId);
    }

    @Override
    public UserEntity suspend(long userId) {
        userService.setRegistrationState(userId, SUSPENDED);
        return userService.readOne(userId);
    }

    @Override
    public UserEntity delete(long userId) {
        userService.setRegistrationState(userId, DELETED);
        return userService.readOne(userId);
    }

    @Override
    public UserEntity registerPlayer(String username, String password) {
        UserEntity user = UserEntity.builder()
            .username(username.trim())
            .authorities(PLAYER.getAuthority())
            .password(password)
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }

    @Override
    public UserEntity ensureActuator() {
        return ensureUser(cfg.getDefaultUserActuatorUsername(), cfg.getDefaultUserActuatorPassword(), ACTUATOR, PLAYER);
    }

    @Override
    public UserEntity ensureAdmin() {
        return ensureUser(cfg.getDefaultUserAdminUsername(), cfg.getDefaultUserAdminPassword(), UserRole.values());
    }

    public UserEntity ensureUser(String username, String password, UserRole... roles) {
        Optional<UserEntity> existingUser = userService.findByUsername(username);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        UserEntity user = UserEntity.builder()
            .username(username)
            .authorities(Stream.of(roles).map(UserRole::getAuthority).collect(Collectors.joining(",")))
            .password(password)
            .registrationState(ACTIVE)
            .build();
        return userService.create(user);
    }
}
