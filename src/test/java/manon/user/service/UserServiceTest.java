package manon.user.service;

import manon.app.security.PasswordEncoderService;
import manon.user.document.User;
import manon.user.err.PasswordNotMatchException;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.model.RegistrationState;
import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest extends AbstractInitBeforeClass {
    
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
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailExistOrFail() throws UserNotFoundException {
        userService.existOrFail(userId(1), UNKNOWN_ID);
    }
    
    @Test
    public void shouldReadOne() throws UserNotFoundException {
        assertThat(userService.readOne(userId(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadOne() throws UserNotFoundException {
        userService.readOne(UNKNOWN_ID);
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
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadByUsername() throws UserNotFoundException {
        userService.readByUsername(UNKNOWN_USER_NAME);
    }
    
    @Test
    public void shouldReadVersionById() throws UserNotFoundException {
        assertThat(userService.readVersionById(userId(2)).getVersion()).isGreaterThanOrEqualTo(0L);
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadVersionById() throws UserNotFoundException {
        userService.readVersionById(UNKNOWN_ID);
    }
    
    @Test
    public void shouldReadIdByUsername() throws UserNotFoundException {
        assertThat(userService.readIdByUsername(name(1)).getId()).isEqualTo(userId(1));
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadIdByUsername() throws UserNotFoundException {
        userService.readIdByUsername(UNKNOWN_USER_NAME);
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
    
    @Test(expectedExceptions = UserExistsException.class)
    public void shouldFailCreate() throws UserExistsException, UserNotFoundException {
        userService.create(userService.readOne(userId(1)));
    }
    
    @Test
    public void shouldSetAndCheckEncodedPassword() throws UserNotFoundException {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        assertThat(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword())).isTrue();
    }
    
    @DataProvider
    public static Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }
    
    @Test(dataProvider = "dataProviderRegistrationStates")
    public void shouldSetRegistrationState(RegistrationState registrationState) throws UserNotFoundException {
        userService.setRegistrationState(userId(4), registrationState);
        assertThat(userService.readOne(userId(4)).getRegistrationState()).isEqualTo(registrationState);
    }
    
    @Test(dataProvider = "dataProviderRegistrationStates")
    public void shouldNotFailWhenSetRegistrationStateOfUnknownUser(RegistrationState registrationState) {
        userService.setRegistrationState(UNKNOWN_ID, registrationState);
    }
    
    @DataProvider
    public static Object[] dataProviderValidPasswords() {
        return new String[][]{
            {"", ""},
            {"@ p4ssword!", "@ p4ssword!"}
        };
    }
    
    @Test(dataProvider = "dataProviderValidPasswords")
    public void shoudValidatePassword(String rawPassword, String passwordToEncode) throws PasswordNotMatchException {
        userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode));
    }
    
    @DataProvider
    public static Object[] dataProviderInvalidPasswords() {
        return new String[][]{
            {"a password!", "@ p4ssword!"},
            {"", "a"},
            {null, "a"}
        };
    }
    
    @Test(dataProvider = "dataProviderInvalidPasswords", expectedExceptions = PasswordNotMatchException.class)
    public void shoudNotValidatePassword(String rawPassword, String passwordToEncode) throws PasswordNotMatchException {
        userService.validatePassword(rawPassword, passwordEncoderService.encode(passwordToEncode));
    }
}
