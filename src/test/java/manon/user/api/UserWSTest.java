package manon.user.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.form.RegistrationForm;
import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserUpdateForm;
import manon.user.service.UserService;
import manon.util.basetest.AbstractInitBeforeTest;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static manon.app.config.AbstractControllerAdvice.FIELD_ERRORS;
import static manon.user.model.RegistrationState.ACTIVE;
import static manon.user.model.RegistrationState.DELETED;
import static manon.user.model.UserAuthority.ROLE_PLAYER;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UserWSTest extends AbstractInitBeforeTest {
    
    @Autowired
    protected UserService userService;
    
    @DataProvider
    public static Object[][] dataProviderShouldRegister() {
        return new Object[][]{
            {"JOHN", "12300"},
            {"BOB DYLAN AND FRIENDS", "PASSWORD"},
            {"A B CX_Y-Z", "secret  p4ssword!"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldRegister")
    public void shouldRegister(String name, String pwd) throws UserNotFoundException {
        whenAnonymous().getRequestSpecification()
            .body(RegistrationForm.builder().username(name).password(pwd).build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        User user = userService.readByUsername(name);
        assertEquals(user.getUsername(), name);
        assertThat(user.getRoles()).containsExactly(ROLE_PLAYER);
        assertThat(user.getVersion()).isGreaterThanOrEqualTo(0L);
        assertEquals(user.getRegistrationState(), ACTIVE);
        assertNotEquals(pwd, user.getPassword(), "don't store raw passwords!"); // IMPORTANT always hash stored password
    }
    
    @Test
    public void shouldNotRegisterTwice() {
        whenAnonymous().getRequestSpecification()
            .body(RegistrationForm.builder().username("DUPLICATE").password("12300").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        whenAnonymous().getRequestSpecification()
            .body(RegistrationForm.builder().username("DUPLICATE").password("12300").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CONFLICT).contentType(JSON)
            .body(FIELD_ERRORS, Matchers.equalTo(UserExistsException.class.getSimpleName()));
    }
    
    @Test
    public void shouldDeleteAndLooseAuthorisations() throws Exception {
        whenP1().getRequestSpecification()
            .get(API_USER).then()
            .statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .delete(API_USER).then()
            .statusCode(SC_OK);
        assertEquals(userService.readOne(userId(1)).getRegistrationState(), DELETED);
        whenP1().getRequestSpecification()
            .get(API_USER).then()
            .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldNotDeleteTwice() throws Exception {
        whenP1().getRequestSpecification()
            .delete(API_USER).then()
            .statusCode(SC_OK);
        assertEquals(userService.readOne(userId(1)).getRegistrationState(), DELETED);
        whenP1().getRequestSpecification()
            .delete(API_USER).then()
            .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldRead() throws Exception {
        Response res = whenP1().getRequestSpecification()
            .get(API_USER);
        res.then()
            .statusCode(SC_OK);
        User webUser = readValue(res, User.class);
        User dbUser = userService.readOne(userId(1)).toBuilder().password(null).build();
        assertEquals(webUser, dbUser);
    }
    
    @Test
    public void shouldReadVersion() throws Exception {
        Response res = whenP1().getRequestSpecification()
            .get(API_USER + "/version");
        res.then()
            .statusCode(SC_OK);
        Long webUserVersion = readValue(res, Long.class);
        Long dbUserVersion = userService.readOne(userId(1)).getVersion();
        assertEquals(webUserVersion, dbUserVersion);
    }
    
    @DataProvider
    public Object[][] dataProviderShouldUpdate() {
        return new Object[][]{
            {"", ""},
            {"nickname", "test.foo@bar.com"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldUpdate")
    public void shouldUpdate(String nickname, String email) throws Exception {
        User userBefore = userService.readOne(userId(1));
        whenP1().getRequestSpecification()
            .body(UserUpdateForm.builder().nickname(nickname).email(email).build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(SC_OK);
        User userAfter = userService.readOne(userId(1));
        User userExpected = userBefore.toBuilder()
            .email(email)
            .nickname(nickname)
            .version(userBefore.getVersion() + 1)
            .build();
        assertEquals(userAfter, userExpected);
    }
    
    @Test
    public void shouldUpdatePassword() {
        whenP1().getRequestSpecification()
            .body(UserPasswordUpdateForm.builder().oldPassword(pwd(1)).newPassword("a new password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_OK);
        RestAssured.given().auth().basic(name(1), "a new password")
            .get(API_USER)
            .then().statusCode(SC_OK);
    }
    
    @Test
    public void shouldNotUpdateBadPassword() {
        whenP1().getRequestSpecification()
            .body(UserPasswordUpdateForm.builder().oldPassword("wrongpassword").newPassword("a new password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(SC_BAD_REQUEST);
        RestAssured.given().auth().basic(name(1), "a new password")
            .get(API_USER)
            .then().statusCode(SC_UNAUTHORIZED);
        RestAssured.given().auth().basic(name(1), pwd(1))
            .get(API_USER)
            .then().statusCode(SC_OK);
    }
    
    /** Only {@link User.Validation#MAX_EVENTS} most recent user's friendshipEvents should be kept. */
    @Test
    public void shouldCheckEventsMaintenance() throws Exception {
        String p1UserId = userId(1);
        String p2UserId = userId(2);
        assertEquals(0, userService.readOne(p1UserId).getFriendshipEvents().size());
        for (int i = 0; i < (User.Validation.MAX_EVENTS / 2) + 5; i++) {
            whenP1().getRequestSpecification()
                .post(API_USER + "/askfriendship/user/" + p2UserId)
                .then()
                .statusCode(SC_OK);
            whenP1().getRequestSpecification()
                .post(API_USER + "/cancelfriendship/user/" + p2UserId)
                .then()
                .statusCode(SC_OK);
            
        }
        assertEquals(userService.readOne(p1UserId).getFriendshipEvents().size(), User.Validation.MAX_EVENTS);
    }
}
