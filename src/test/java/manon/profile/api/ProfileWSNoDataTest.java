package manon.profile.api;

import com.jayway.restassured.http.ContentType;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationForm;
import manon.user.service.UserService;
import manon.util.basetest.InitBeforeClass;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static manon.app.config.ControllerAdvice.FIELD_ERRORS;
import static manon.app.config.ControllerAdvice.FIELD_MESSAGE;
import static manon.user.UserAuthority.PLAYER;
import static manon.user.registration.RegistrationStateEnum.ACTIVE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class ProfileWSNoDataTest extends InitBeforeClass {
    
    @Autowired
    protected UserService userService;
    
    @DataProvider
    public static Object[][] dataProviderNamesAndPasswords() {
        return new Object[][]{
                {"JOHN", "12300"},
                {"BOB DYLAN AND FRIENDS", "PASSWORD"},
                {"A B CX_Y-Z", "secret  p4ssword!"}
        };
    }
    
    @Test(dataProvider = "dataProviderNamesAndPasswords")
    public void shouldRegister(String name, String pwd) throws Exception {
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm(name, pwd))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_CREATED);
        User user = userService.findByUsername(name).orElseThrow(UserNotFoundException::new);
        assertEquals(user.getUsername(), name);
        assertThat(user.getRoles()).containsExactly(PLAYER);
        assertEquals(user.getVersion(), 0);
        assertEquals(user.getRegistrationState(), ACTIVE);
        assertNotEquals(pwd, user.getPassword(), "don't store raw passwords!"); // IMPORTANT always hash stored password
    }
    
    @Test
    public void shouldNotregisterTwice() {
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("DUPLICATE", "12300"))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_CREATED);
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("DUPLICATE", "45600"))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_CONFLICT).contentType(ContentType.JSON)
                .body(FIELD_ERRORS, Matchers.equalTo("UserExistsException"),
                        FIELD_MESSAGE, Matchers.equalTo("DUPLICATE"));
    }
    
    /** Username is not case sensitive, so we can't register twice with different case. */
    @Test
    public void shouldNotRegisterDifferentCases() {
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("CASE1", "12300"))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_CREATED);
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("Case1", "12300"))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                .body(FIELD_ERRORS, Matchers.equalTo("RegistrationFormException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains("USERNAME_BAD_FORMAT"));
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("case1", "12300"))
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_PROFILE)
                .then()
                .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                .body(FIELD_ERRORS, Matchers.equalTo("RegistrationFormException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains("USERNAME_BAD_FORMAT"));
    }
}
