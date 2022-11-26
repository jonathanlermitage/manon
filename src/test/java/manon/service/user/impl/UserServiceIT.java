package manon.service.user.impl;

import manon.document.user.UserEntity;
import manon.document.user.UserSnapshotEntity;
import manon.dto.user.UserWithSnapshotsDto;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.mapper.user.UserMapper;
import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import manon.util.basetest.AbstractIT;
import net.datafaker.Faker;
import net.datafaker.providers.base.Name;
import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static manon.document.user.UserEntity.Validation.USERNAME_MAX_LENGTH;

class UserServiceIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 4;
    }

    @Test
    void shouldExistOrFail() {
        userService.existOrFail(userId(1));
    }

    @Test
    void shouldFailExistOrFailUnknown() {
        long uid1 = userId(1);
        Assertions.assertThatThrownBy(() -> userService.existOrFail(uid1, UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReadOne() {
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shouldReadOneFailReadLazyDataOutsideASession() {
        UserEntity dbUser = userService.readOne(userId(1));
        List<UserSnapshotEntity> lazyUserSnapshots = dbUser.getUserSnapshots();
        Assertions.assertThatThrownBy(lazyUserSnapshots::size)
            .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    void shouldReadOneWhenUserHasSnapshots() {
        userSnapshotService.persistAll(Arrays.asList(
            UserMapper.MAPPER.toUserSnapshotEntity(user(1)),
            UserMapper.MAPPER.toUserSnapshotEntity(user(1))
        ));
        UserEntity dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shouldReadOneWhenUserHasSnapshotsFailReadLazyDataOutsideASession() {
        userSnapshotService.persistAll(Arrays.asList(
            UserMapper.MAPPER.toUserSnapshotEntity(user(1)),
            UserMapper.MAPPER.toUserSnapshotEntity(user(1))
        ));
        UserEntity dbUser = userService.readOne(userId(1));
        List<UserSnapshotEntity> lazyUserSnapshots = dbUser.getUserSnapshots();
        Assertions.assertThatThrownBy(lazyUserSnapshots::size)
            .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    void shouldReadOneAndFetchUserSnapshotDtos() {
        UserWithSnapshotsDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
        Assertions.assertThat(dbUser.getUserSnapshots()).isEmpty();
    }

    @Test
    void shouldReadOneAndFetchUserSnapshotDtosWhenUserHasSnapshots() {
        userSnapshotService.persistAll(Arrays.asList(
            UserMapper.MAPPER.toUserSnapshotEntity(user(1)),
            UserMapper.MAPPER.toUserSnapshotEntity(user(1))
        ));
        UserWithSnapshotsDto dbUser = userService.readOneAndFetchUserSnapshotDtos(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
        Assertions.assertThat(dbUser.getUserSnapshots()).hasSize(2);
    }

    @Test
    void shouldFailReadOneUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readOne(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldFindByUsername() throws Exception {
        Assertions.assertThat(userService.findByUsername(name(1)).orElseThrow(Exception::new).getId()).isEqualTo(userId(1));
    }

    @Test
    void shouldNotFindByUsername() {
        Assertions.assertThat(userService.findByUsername(UNKNOWN_USER_NAME)).isNotPresent();
    }

    @Test
    void shouldReadByUsername() {
        Assertions.assertThat(userService.readByUsername(name(1)).getId()).isEqualTo(userId(1));
    }

    @Test
    void shouldFailReadByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReadVersionById() {
        Assertions.assertThat(userService.readVersionById(userId(2)).getVersion()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void shouldFailReadVersionByIdUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readVersionById(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReadIdByUsername() {
        Assertions.assertThat(userService.readIdByUsername(name(1)).getId()).isEqualTo(userId(1));
    }

    @Test
    void shouldFailReadIdByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readIdByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldCreateAndCheckEncodedPassword() {
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
    void shouldFailCreateExisting() {
        UserEntity existingUser = userService.readOne(userId(1));
        Assertions.assertThatThrownBy(() -> userService.create(existingUser))
            .isInstanceOf(UserExistsException.class);
    }

    @Test
    void shouldSetAndCheckEncodedPassword() {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        Assertions.assertThat(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword())).isTrue();
    }

    Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }

    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    void shouldSetRegistrationState(RegistrationState registrationState) {
        userService.setRegistrationState(userId(4), registrationState);
        Assertions.assertThat(userService.readOne(userId(4)).getRegistrationState()).isEqualTo(registrationState);
    }

    @SuppressWarnings("CatchMayIgnoreException")
    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    void shouldNotFailWhenSetRegistrationStateOfUnknownUser(RegistrationState registrationState) {
        try {
            userService.setRegistrationState(UNKNOWN_ID, registrationState);
        } catch (Throwable e) {
            Assertions.fail("should be allowed to set registration on unknown user", e);
        }
    }

    Object[] dataProviderValidPasswords() {
        return new String[][]{
            {"", ""},
            {"@ p4ssword!", "@ p4ssword!"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderValidPasswords")
    void shoudValidatePassword(String rawPassword, String passwordToEncode) {
        userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode));
    }

    Object[] dataProviderInvalidPasswords() {
        return new String[][]{
            {"a password!", "@ p4ssword!"},
            {"", "a"},
            {null, "a"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderInvalidPasswords")
    void shoudNotValidatePassword(String rawPassword, String passwordToEncode) {
        String encodedPassword = passwordEncoderService.encode(passwordToEncode);
        Assertions.assertThatThrownBy(() -> userService.validatePassword(rawPassword, encodedPassword))
            .isInstanceOf(PasswordNotMatchException.class);
    }

    Object[] dataProviderUsernames() {
        Faker faker = new Faker();
        Set<String> names = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            Name name = faker.name();
            String validUsername = name.nameWithMiddle().toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z_]+", "");
            if (validUsername.length() > USERNAME_MAX_LENGTH) {
                validUsername = validUsername.substring(0, USERNAME_MAX_LENGTH);
            }
            names.add(validUsername);
        }
        return names.toArray();
    }

    @ParameterizedTest
    @MethodSource("dataProviderUsernames")
    void shouldSave(String validUsername) {
        LocalDateTime before = Tools.now();
        userService.persist(UserEntity.builder()
            .username(validUsername)
            .password("password")
            .registrationState(RegistrationState.ACTIVE)
            .authorities(UserRole.PLAYER.name())
            .email("email@domain.com")
            .nickname("nickname")
            .build());
        LocalDateTime after = Tools.now();

        UserEntity user = userService.readByUsername(validUsername);
        Assertions.assertThat(user.getCreationDate()).isBetween(before, after);
        Assertions.assertThat(user.getUpdateDate()).isBetween(before, after);
        Assertions.assertThat(user.getVersion()).isNotNegative();
    }
}
