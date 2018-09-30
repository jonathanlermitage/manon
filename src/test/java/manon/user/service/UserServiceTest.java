package manon.user.service;

import manon.app.security.PasswordEncoderService;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.model.RegistrationState;
import manon.util.basetest.InitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.lang.System.currentTimeMillis;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class UserServiceTest extends InitBeforeClass {
    
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
        assertEquals(userService.readOne(userId(1)).getId(), userId(1));
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadOne() throws UserNotFoundException {
        userService.readOne(UNKNOWN_ID);
    }
    
    @Test
    public void shouldFindByUsername() throws Exception {
        assertEquals(userService.findByUsername(name(1)).orElseThrow(Exception::new).getId(), userId(1));
    }
    
    @Test
    public void shouldNotFindByUsername() {
        assertFalse(userService.findByUsername(UNKNOWN_USER_NAME).isPresent());
    }
    
    @Test
    public void shouldReadByUsername() throws UserNotFoundException {
        assertEquals(userService.readByUsername(name(1)).getId(), userId(1));
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadByUsername() throws UserNotFoundException {
        userService.readByUsername(UNKNOWN_USER_NAME);
    }
    
    @Test
    public void shouldReadVersionById() throws UserNotFoundException {
        assertEquals(userService.readVersionById(userId(2)).getVersion(), 0L);
    }
    
    @Test(expectedExceptions = UserNotFoundException.class)
    public void shouldFailReadVersionById() throws UserNotFoundException {
        userService.readVersionById(UNKNOWN_ID);
    }
    
    @Test
    public void shouldReadIdByUsername() throws UserNotFoundException {
        assertEquals(userService.readIdByUsername(name(1)).getId(), userId(1));
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
        assertNotEquals(user.getPassword(), rawPassword);
        assertTrue(passwordEncoderService.getEncoder().matches(rawPassword, user.getPassword()));
    }
    
    @Test(expectedExceptions = UserExistsException.class)
    public void shouldFailCreate() throws UserExistsException, UserNotFoundException {
        userService.create(userService.readOne(userId(1)));
    }
    
    @Test
    public void shouldSetAndCheckEncodedPassword() throws UserNotFoundException {
        String rawPassword = "pwd" + currentTimeMillis();
        userService.encodeAndSetPassword(userId(3), rawPassword);
        assertTrue(passwordEncoderService.getEncoder().matches(rawPassword, userService.readOne(userId(3)).getPassword()));
    }
    
    @DataProvider
    public static Object[] dataProviderRegistrationStates() {
        return RegistrationState.values();
    }
    
    @Test(dataProvider = "dataProviderRegistrationStates")
    public void shouldSetRegistrationState(RegistrationState registrationState) throws UserNotFoundException {
        userService.setRegistrationState(userId(4), registrationState);
        assertEquals(userService.readOne(userId(4)).getRegistrationState(), registrationState);
    }
    
    @Test(dataProvider = "dataProviderRegistrationStates")
    public void shouldNotFailWhenSetRegistrationStateOfUnknownUser(RegistrationState registrationState) {
        userService.setRegistrationState(UNKNOWN_ID, registrationState);
    }
}
