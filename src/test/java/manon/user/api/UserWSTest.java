package manon.user.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import manon.user.UserExistsException;
import manon.user.document.User;
import manon.user.form.UserFieldEnum;
import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserPasswordUpdateFormException;
import manon.user.form.UserUpdateForm;
import manon.user.form.UserUpdateFormException;
import manon.user.registration.form.RegistrationForm;
import manon.user.registration.form.RegistrationFormException;
import manon.user.service.UserService;
import manon.util.basetest.InitBeforeTest;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static manon.app.config.ControllerAdviceBase.FIELD_ERRORS;
import static manon.app.config.ControllerAdviceBase.FIELD_MESSAGE;
import static manon.user.UserAuthority.PLAYER;
import static manon.user.registration.RegistrationStateEnum.ACTIVE;
import static manon.user.registration.RegistrationStateEnum.DELETED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UserWSTest extends InitBeforeTest {
    
    @Autowired
    protected UserService userService;
    
    @DataProvider
    public static Object[][] dataProviderRegister() {
        return new Object[][]{
                {"JOHN", "12300"},
                {"BOB DYLAN AND FRIENDS", "PASSWORD"},
                {"A B CX_Y-Z", "secret  p4ssword!"}
        };
    }
    
    @Test(dataProvider = "dataProviderRegister")
    public void shouldRegister(String name, String pwd) {
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm(name, pwd))
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(SC_CREATED);
        User user = userService.readByUsername(name);
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
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(SC_CREATED);
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm("DUPLICATE", "45600"))
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(SC_CONFLICT).contentType(JSON)
                .body(FIELD_ERRORS, Matchers.equalTo(UserExistsException.class.getSimpleName()),
                        FIELD_MESSAGE, Matchers.equalTo("DUPLICATE"));
    }
    
    @DataProvider
    public Object[][] dataProviderNotRegisterInvalidData() {
        return new Object[][]{
                {"Username", "", "USERNAME_BAD_FORMAT", "PASSWORD_EMPTY"},
                {"Username", "123456", "USERNAME_BAD_FORMAT"},
                {"username", "123456", "USERNAME_BAD_FORMAT"},
                {"USERNaME", "123456", "USERNAME_BAD_FORMAT"},
                {"USERN*ME", "123456", "USERNAME_BAD_FORMAT"},
                {"<USERNAME", "123456", "USERNAME_BAD_FORMAT"},
                {"USERN>ME", "123456", "USERNAME_BAD_FORMAT"},
                {null, "123456", "USERNAME_EMPTY"},
                {"", "123456", "USERNAME_EMPTY"},
                {"U", "123456", "USERNAME_TOO_SHORT"},
                {verylongString("U"), "123456", "USERNAME_TOO_LONG"},
                {"USERNAME", null, "PASSWORD_EMPTY"},
                {"USERNAME", "", "PASSWORD_EMPTY"},
                {"USERNAME", "1", "PASSWORD_TOO_SHORT"},
                {"USERNAME", verylongString("1"), "PASSWORD_TOO_LONG"}
        };
    }
    
    @Test(dataProvider = "dataProviderNotRegisterInvalidData")
    public void shouldNotRegisterInvalidData(String username, String pwd, String[] errMsg) {
        whenAnonymous().getRequestSpecification()
                .body(new RegistrationForm(username, pwd))
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(SC_BAD_REQUEST).contentType(JSON)
                .body(FIELD_ERRORS, Matchers.equalTo(RegistrationFormException.class.getSimpleName()),
                        FIELD_MESSAGE, Matchers.hasSize(errMsg.length),
                        FIELD_MESSAGE, Matchers.contains(errMsg));
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
    public Object[][] dataProviderShouldUpdateNickname() {
        return new Object[][]{
                {""},
                {"a new nickname"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldUpdateNickname")
    public void shouldUpdateNickname(String nickname) throws Exception {
        User userBefore = userService.readOne(userId(1));
        whenP1().getRequestSpecification()
                .body(new UserUpdateForm(UserFieldEnum.NICKNAME, nickname))
                .contentType(ContentType.JSON)
                .put(API_USER + "/field")
                .then()
                .statusCode(SC_OK);
        User userAfter = userService.readOne(userId(1));
        userBefore = userBefore.toBuilder()
                .nickname(nickname)
                .version(userBefore.getVersion() + 1)
                .build();
        assertEquals(userAfter, userBefore);
    }
    
    @DataProvider
    public Object[][] dataProviderShouldUpdateEmail() {
        return new Object[][]{
                {""},
                {"test.foo@bar.com"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldUpdateEmail")
    public void shouldUpdateEmail(String email) throws Exception {
        User userBefore = userService.readOne(userId(1));
        whenP1().getRequestSpecification()
                .body(new UserUpdateForm(UserFieldEnum.EMAIL, email))
                .contentType(ContentType.JSON)
                .put(API_USER + "/field")
                .then()
                .statusCode(SC_OK);
        User userAfter = userService.readOne(userId(1));
        userBefore = userBefore.toBuilder()
                .email(email)
                .version(userBefore.getVersion() + 1)
                .build();
        assertEquals(userAfter, userBefore);
    }
    
    
    @DataProvider
    public Object[][] dataProviderNotUpdateInvalidData() {
        return new Object[][]{
                {UserFieldEnum.EMAIL, null, "EMAIL_NULL"},
                {UserFieldEnum.EMAIL, "test.foo.bar.com", "EMAIL_BAD_FORMAT"},
                {UserFieldEnum.EMAIL, 1, "EMAIL_BAD_CLASS"},
                {UserFieldEnum.EMAIL, verylongString("averylongemail") + "@test.com", "EMAIL_TOO_LONG"},
                {UserFieldEnum.NICKNAME, null, "NICKNAME_NULL"},
                {UserFieldEnum.NICKNAME, "anickname!!!", "NICKNAME_BAD_FORMAT"},
                {UserFieldEnum.NICKNAME, 1, "NICKNAME_BAD_CLASS"},
                {UserFieldEnum.NICKNAME, verylongString("averylongnickname"), "NICKNAME_TOO_LONG"}
        };
    }
    
    @Test(dataProvider = "dataProviderNotUpdateInvalidData")
    public void shouldNotUpdateInvalidData(UserFieldEnum field, Object value, String[] errMsg) {
        whenP1().getRequestSpecification()
                .body(new UserUpdateForm(field, value))
                .contentType(ContentType.JSON)
                .put(API_USER + "/field")
                .then()
                .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                .body(FIELD_ERRORS, Matchers.equalTo(UserUpdateFormException.class.getSimpleName()),
                        FIELD_MESSAGE, Matchers.hasSize(errMsg.length),
                        FIELD_MESSAGE, Matchers.contains(errMsg));
    }
    
    @Test
    public void shouldUpdatePassword() {
        whenP1().getRequestSpecification()
                .body(new UserPasswordUpdateForm(pwd(1), "a new password", "a new password"))
                .contentType(ContentType.JSON)
                .put(API_USER + "/password")
                .then()
                .statusCode(SC_OK);
        RestAssured.given().auth().basic(name(1), "a new password")
                .get(API_USER)
                .then().statusCode(SC_OK);
    }
    
    @DataProvider
    public Object[][] dataProviderNotUpdatePasswordInvalidData() {
        return new Object[][]{
                {pwd(1), "a new password", "a different password", "NEW_PASSWORD_NOT_CHECKED"},
                {"", "a new password", "a new password", "OLD_PASSWORD_EMPTY"},
                {null, "a new password", "a new password", "OLD_PASSWORD_EMPTY"},
                {pwd(0), "", "a different password", "NEW_PASSWORD_EMPTY", "NEW_PASSWORD_NOT_CHECKED"},
                {pwd(1), "a new password", "", "CHECK_PASSWORD_EMPTY", "NEW_PASSWORD_NOT_CHECKED"},
                {pwd(1), "", "", "NEW_PASSWORD_EMPTY", "CHECK_PASSWORD_EMPTY"},
                {pwd(1), "a new password", "", "CHECK_PASSWORD_EMPTY", "NEW_PASSWORD_NOT_CHECKED"},
                {pwd(1), pwd(1), pwd(1), "OLD_PASSWORD_EQUALS_NEW"},
                {pwd(1), pwd(1), "a new password", "OLD_PASSWORD_EQUALS_NEW", "NEW_PASSWORD_NOT_CHECKED"},
                {pwd(1), "a new password", pwd(1), "NEW_PASSWORD_NOT_CHECKED"},
                {pwd(1), null, null, "NEW_PASSWORD_EMPTY", "CHECK_PASSWORD_EMPTY"}
        };
    }
    
    @Test(dataProvider = "dataProviderNotUpdatePasswordInvalidData")
    public void shouldNotUpdatePasswordInvalidData(String oldPwd, String newPwd, String chkPwd, String[] errMsg) {
        whenP1().getRequestSpecification()
                .body(new UserPasswordUpdateForm(oldPwd, newPwd, chkPwd))
                .contentType(ContentType.JSON)
                .put(API_USER + "/password")
                .then()
                .statusCode(SC_BAD_REQUEST).contentType(ContentType.JSON)
                .body(FIELD_ERRORS, Matchers.equalTo(UserPasswordUpdateFormException.class.getSimpleName()),
                        FIELD_MESSAGE, Matchers.hasSize(errMsg.length),
                        FIELD_MESSAGE, Matchers.contains(errMsg));
        // invalid operations should not be effective
        whenP1().getRequestSpecification()
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
