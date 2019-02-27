package manon.user.api;

import io.restassured.response.ValidatableResponse;
import manon.user.form.RegistrationForm;
import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserUpdateForm;
import manon.util.basetest.AbstractAccessControlTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static manon.user.document.User.Validation.EMAIL_SIZE_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_PATTERN_ERRMSG;
import static manon.user.document.User.Validation.NICKNAME_SIZE_ERRMSG;
import static manon.user.document.User.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.user.document.User.Validation.USERNAME_PATTERN_ERRMSG;
import static manon.user.document.User.Validation.USERNAME_SIZE_ERRMSG;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class UserWSValidTest extends AbstractAccessControlTest {
    
    public Object[][] dataProviderShouldValidateRegister() {
        return new Object[][]{
            {SC_CREATED, "VALID_USERNAME", "a valid password", null},
            {SC_BAD_REQUEST, "", "", new String[]{USERNAME_SIZE_ERRMSG, PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "Username", "", new String[]{USERNAME_PATTERN_ERRMSG, PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "Username", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "username", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "USERNaME", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "USERN*ME", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "<USERNAME", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "USERN>ME", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, "<b>USERNME</b>", "123456", new String[]{USERNAME_PATTERN_ERRMSG}},
            {SC_BAD_REQUEST, null, "123456", new String[]{USERNAME_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "", "123456", new String[]{USERNAME_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "U", "123456", new String[]{USERNAME_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, verylongString("U"), "123456", new String[]{USERNAME_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", null, new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", "", new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", "1", new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", verylongString("1"), new String[]{PASSWORD_SIZE_ERRMSG}}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateRegister")
    public void shouldValidateRegister(int statusCode, String username, String pwd, String[] errMsg) {
        ValidatableResponse response = whenAnonymous().getRequestSpecification()
            .body(RegistrationForm.builder().username(username).password(pwd).build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(ERRORS_MSG, Matchers.hasSize(errMsg.length),
                ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }
    
    public Object[][] dataProviderShouldValidateUpdate() {
        return new Object[][]{
            {SC_OK, null, null, null},
            {SC_OK, "", "", null},
            {SC_OK, "", "valid@email.com", null},
            {SC_OK, "a valid nickname", "", null},
            {SC_OK, "a valid nickname", "valid@email.com", null},
            {SC_BAD_REQUEST, "", verylongString("averylongemail") + "@test.com", EMAIL_SIZE_ERRMSG},
            {SC_BAD_REQUEST, "anickname!!!", "", NICKNAME_PATTERN_ERRMSG},
            {SC_BAD_REQUEST, verylongString("averylongnickname"), "", NICKNAME_SIZE_ERRMSG}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateUpdate")
    public void shouldValidateUpdate(int statusCode, String nickname, String email, String errMsg) {
        ValidatableResponse response = whenP1().getRequestSpecification()
            .body(UserUpdateForm.builder().nickname(nickname).email(email).build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(ERRORS_MSG, Matchers.hasSize(1),
                ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }
    
    public Object[][] dataProviderShouldValidateUpdatePassword() {
        return new Object[][]{
            {SC_OK, pwd(1), "a new valid password", null},
            {SC_BAD_REQUEST, pwd(1), "", PASSWORD_SIZE_ERRMSG},
            {SC_BAD_REQUEST, pwd(1), null, PASSWORD_SIZE_ERRMSG},
            {SC_BAD_REQUEST, pwd(1), verylongString("averylongpassword"), PASSWORD_SIZE_ERRMSG}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateUpdatePassword")
    public void shouldValidateUpdatePassword(int statusCode, String oldPwd, String newPwd, String errMsg) {
        ValidatableResponse response = whenP1().getRequestSpecification()
            .body(UserPasswordUpdateForm.builder().oldPassword(oldPwd).newPassword(newPwd).build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(ERRORS_MSG, Matchers.hasSize(1),
                ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }
}
