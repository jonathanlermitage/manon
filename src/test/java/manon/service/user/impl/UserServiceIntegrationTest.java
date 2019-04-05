package manon.service.user.impl;

import manon.document.user.User;
import manon.document.user.UserSnapshot;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.model.user.RegistrationState;
import manon.model.user.UserRole;
import manon.util.Tools;
import manon.util.basetest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;

public class UserServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Override
    public int getNumberOfUsers() {
        return 4;
    }
    
    @Test
    public void shouldExistOrFail() throws Exception {
        userService.existOrFail(userId(1));
    }
    
    @Test
    public void shouldFailExistOrFailUnknown() {
        Assertions.assertThatThrownBy(() -> userService.existOrFail(userId(1), UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadOne() throws Exception {
        User dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldReadOneFailReadLazyDataOutsideASession() throws Exception {
        User dbUser = userService.readOne(userId(1));
        Assertions.assertThatThrownBy(() -> dbUser.getUserSnapshots().size())
            .isInstanceOf(LazyInitializationException.class);
    }
    
    @Test
    public void shouldReadOneWhenUserHasSnapshots() throws Exception {
        userSnapshotService.save(Arrays.asList(
            UserSnapshot.from(user(1)),
            UserSnapshot.from(user(1))
        ));
        User dbUser = userService.readOne(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldReadOneWhenUserHasSnapshotsFailReadLazyDataOutsideASession() throws Exception {
        userSnapshotService.save(Arrays.asList(
            UserSnapshot.from(user(1)),
            UserSnapshot.from(user(1))
        ));
        User dbUser = userService.readOne(userId(1));
        Assertions.assertThatThrownBy(() -> dbUser.getUserSnapshots().size())
            .isInstanceOf(LazyInitializationException.class);
    }
    
    @Test
    public void shouldReadOneAndFetchUserSnapshots() throws Exception {
        User dbUser = userService.readOneAndFetchUserSnapshots(userId(1));
        Assertions.assertThat(dbUser.getId()).isEqualTo(userId(1));
        Assertions.assertThat(dbUser.getUserSnapshots()).isEmpty();
    }
    
    @Test
    public void shouldReadOneAndFetchUserSnapshotsWhenUserHasSnapshots() throws Exception {
        userSnapshotService.save(Arrays.asList(
            UserSnapshot.from(user(1)),
            UserSnapshot.from(user(1))
        ));
        User dbUser = userService.readOneAndFetchUserSnapshots(userId(1));
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
    public void shouldReadByUsername() throws Exception {
        Assertions.assertThat(userService.readByUsername(name(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldFailReadByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadVersionById() throws Exception {
        Assertions.assertThat(userService.readVersionById(userId(2)).getVersion()).isGreaterThanOrEqualTo(0L);
    }
    
    @Test
    public void shouldFailReadVersionByIdUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readVersionById(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadIdByUsername() throws Exception {
        Assertions.assertThat(userService.readIdByUsername(name(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldFailReadIdByUsernameUnknown() {
        Assertions.assertThatThrownBy(() -> userService.readIdByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldCreateAndCheckEncodedPassword() throws Exception {
        String rawPassword = "pwd" + currentTimeMillis();
        User user = userService.create(User.builder()
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
    public void shouldSetAndCheckEncodedPassword() throws Exception {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        Assertions.assertThat(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword())).isTrue();
    }
    
    public Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    public void shouldSetRegistrationState(RegistrationState registrationState) throws Exception {
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
    public void shoudValidatePassword(String rawPassword, String passwordToEncode) throws Exception {
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
    public void shouldSave() throws Exception {
        LocalDateTime before = Tools.now();
        userService.save(User.builder()
            .username("SHOULD_SAVE_USERNAME")
            .password("password")
            .registrationState(RegistrationState.ACTIVE)
            .authorities(UserRole.PLAYER.name())
            .email("email@domain.com")
            .nickname("nickname")
            .build());
        LocalDateTime after = Tools.now();
        
        User user = userService.findByUsername("SHOULD_SAVE_USERNAME").orElseThrow(UserNotFoundException::new);
        Assertions.assertThat(user.getCreationDate()).isBetween(before, after);
        Assertions.assertThat(user.getUpdateDate()).isBetween(before, after);
        Assertions.assertThat(user.getVersion()).isGreaterThanOrEqualTo(0);
    }
}
