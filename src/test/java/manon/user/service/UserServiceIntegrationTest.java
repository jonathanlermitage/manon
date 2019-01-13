package manon.user.service;

import manon.app.security.PasswordEncoderService;
import manon.user.document.User;
import manon.user.err.PasswordNotMatchException;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.model.RegistrationState;
import manon.util.basetest.AbstractInitBeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserServiceIntegrationTest extends AbstractInitBeforeClass {
    
    @Autowired
    private PasswordEncoderService passwordEncoderService;
    
    @Override
    public int getNumberOfUsers() {
        return 4;
    }
    
    @Test
    public void shouldExistOrFail() throws UserNotFoundException {
        userService.existOrFail(userId(1));
    }
    
    @Test
    public void shouldFailExistOrFail() {
        assertThatThrownBy(() -> userService.existOrFail(userId(1), UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadOne() throws UserNotFoundException {
        assertThat(userService.readOne(userId(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldFailReadOne() {
        assertThatThrownBy(() -> userService.readOne(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldFindByUsername() throws Exception {
        assertThat(userService.findByUsername(name(1)).orElseThrow(Exception::new).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldNotFindByUsername() {
        assertThat(userService.findByUsername(UNKNOWN_USER_NAME)).isNotPresent();
    }
    
    @Test
    public void shouldReadByUsername() throws UserNotFoundException {
        assertThat(userService.readByUsername(name(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldFailReadByUsername() {
        assertThatThrownBy(() -> userService.readByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadVersionById() throws UserNotFoundException {
        assertThat(userService.readVersionById(userId(2)).getVersion()).isGreaterThanOrEqualTo(0L);
    }
    
    @Test
    public void shouldFailReadVersionById() {
        assertThatThrownBy(() -> userService.readVersionById(UNKNOWN_ID))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldReadIdByUsername() throws UserNotFoundException {
        assertThat(userService.readIdByUsername(name(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test
    public void shouldFailReadIdByUsername() {
        assertThatThrownBy(() -> userService.readIdByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UserNotFoundException.class);
    }
    
    @Test
    public void shouldCreateAndCheckEncodedPassword() throws UserExistsException {
        String rawPassword = "pwd" + currentTimeMillis();
        User user = userService.create(User.builder()
            .username(name(100))
            .password(rawPassword)
            .build());
        assertThat(user.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoderService.getEncoder().matches(rawPassword, user.getPassword())).isTrue();
    }
    
    @Test
    public void shouldFailCreate() {
        assertThatThrownBy(() -> userService.create(userService.readOne(userId(1))))
            .isInstanceOf(UserExistsException.class);
    }
    
    @Test
    public void shouldSetAndCheckEncodedPassword() throws UserNotFoundException {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        assertThat(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword())).isTrue();
    }
    
    public Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderRegistrationStates")
    public void shouldSetRegistrationState(RegistrationState registrationState) throws UserNotFoundException {
        userService.setRegistrationState(userId(4), registrationState);
        assertThat(userService.readOne(userId(4)).getRegistrationState()).isEqualTo(registrationState);
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
    public void shoudValidatePassword(String rawPassword, String passwordToEncode) throws PasswordNotMatchException {
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
        assertThatThrownBy(() -> userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode)))
            .isInstanceOf(PasswordNotMatchException.class);
    }
}
