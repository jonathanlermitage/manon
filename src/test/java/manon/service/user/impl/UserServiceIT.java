package manon.service.user.impl;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserWithSnapshotsResponseDto;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import manon.util.basetest.AbstractIT;
import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;

public class UserServiceIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 4;
    }

    @Test
    public void shouldExistOrFail() {
        userService.existOrFail(userId(1));
    }

    @Test
    public void shouldFailExistOrFailUnknown() {
        Assertions.assertThatThrownBy(() -> userService.existOrFail(userId(1), UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void shouldReadOne() {
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }

    @Test
    public void shouldReadOneFailReadLazyDataOutsideASession() {
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThatThrownBy(() -> dbUser.getUserSnapshots().size())
            .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void shouldReadOneWhenUserHasSnapshots() {
        userSnapshotService.saveAll(Arrays.asList(
            UserSnapshotEntity.from(user(1)),
            UserSnapshotEntity.from(user(1))
        ));
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }

    @Test
    public void shouldReadOneWhenUserHasSnapshotsFailReadLazyDataOutsideASession() {
        userSnapshotService.saveAll(Arrays.asList(
            UserSnapshotEntity.from(user(1)),
            UserSnapshotEntity.from(user(1))
        ));
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThatThrownBy(() -> dbUser.getUserSnapshots().size())
            .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void shouldReadOneAndFetchUserSnapshotDtos() {
        UserWithSnapshotsResponseDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
        Assertions.assertThat(dbUser.getUserSnapshots()).isEmpty();
    }

    @Test
    public void shouldReadOneAndFetchUserSnapshotDtosWhenUserHasSnapshots() {
        userSnapshotService.saveAll(Arrays.asList(
            UserSnapshotEntity.from(user(1)),
            UserSnapshotEntity.from(user(1))
        ));
        UserWithSnapshotsResponseDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
        Assertions.assertThat(dbUser.getUserSnapshots()).hasSize(2);
    }

    @Test
    public void shouldFailReadOneUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readOne(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void shouldFindByUsername() throws Exception {
        Assertions.assertThat(userService.findByUsername(name(1)).orElseThrow(Exception::new).getId()).isEqualTo(userId(1));
    }

    @Test
    public void shouldNotFindByUsername() {
        Assertions.assertThat(userService.findByUsername(UNKNOWN_USER_NAME)).isNotPresent();
    }

    @Test
    public void shouldReadByUsername() {
        Assertions.assertThat(userService.readByUsername(name(1)).getId()).isEqualTo(userId(1));
    }

    @Test
    public void shouldFailReadByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void shouldReadVersionById() {
        Assertions.assertThat(userService.readVersionById(userId(2)).getVersion()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    public void shouldFailReadVersionByIdUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readVersionById(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void shouldReadIdByUsername() {
        Assertions.assertThat(userService.readIdByUsername(name(1)).getId()).isEqualTo(userId(1));
    }

    @Test
    public void shouldFailReadIdByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readIdByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void shouldCreateAndCheckEncodedPassword() {
        String rawPassword = "pwd" + currentTimeMillis();
        UserEntity user = userService.create(UserEntity.builder()
            .username(name(100))
            .authorities("")
            .password(rawPassword)
            .registrationState(RegistrationState.ACTIVE)
            .build());
        Assertions.assertThat(user.getPassword()).isNotEqualTo(rawPassword);
        Assertions.assertThat(passwordEncoderService.getEncoder().matches(rawPassword, user.getPassword())).isTrue();
    }

    @Test
    public void shouldFailCreateExisting() {
        Assertions.assertThatThrownBy(() -> userService.create(userService.readOne(userId(1))))
            .isInstanceOf(UserExistsException.class);
    }

    @Test
    public void shouldSetAndCheckEncodedPassword() {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        Assertions.assertThat(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword())).isTrue();
    }

    public Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }

    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    public void shouldSetRegistrationState(RegistrationState registrationState) {
        userService.setRegistrationState(userId(4), registrationState);
        Assertions.assertThat(userService.readOne(userId(4)).getRegistrationState()).isEqualTo(registrationState);
    }

    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    public void shouldNotFailWhenSetRegistrationStateOfUnknownUser(RegistrationState registrationState) {
        userService.setRegistrationState(UNKNOWN_ID, registrationState);
    }

    public Object[] dataProviderValidPasswords() {
        return new String[][]{
            {"", ""},
            {"@ p4ssword!", "@ p4ssword!"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderValidPasswords")
    public void shoudValidatePassword(String rawPassword, String passwordToEncode) {
        userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode));
    }

    public Object[] dataProviderInvalidPasswords() {
        return new String[][]{
            {"a password!", "@ p4ssword!"},
            {"", "a"},
            {null, "a"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderInvalidPasswords")
    public void shoudNotValidatePassword(String rawPassword, String passwordToEncode) {
        Assertions.assertThatThrownBy(() -> userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode)))
            .isInstanceOf(PasswordNotMatchException.class);
    }

    @Test
    public void shouldSave() {
        LocalDateTime before = Tools.now();
        userService.save(UserEntity.builder()
            .username("SHOULD_SAVE_USERNAME")
            .password("password")
            .registrationState(RegistrationState.ACTIVE)
            .authorities(UserRole.PLAYER.name())
            .email("email@domain.com")
            .nickname("nickname")
            .build());
        LocalDateTime after = Tools.now();

        UserEntity user = userService.readByUsername("SHOULD_SAVE_USERNAME");
        Assertions.assertThat(user.getCreationDate()).isBetween(before, after);
        Assertions.assertThat(user.getUpdateDate()).isBetween(before, after);
        Assertions.assertThat(user.getVersion()).isGreaterThanOrEqualTo(0);
    }
}
