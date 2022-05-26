package manon.api.user;

import io.restassured.response.ValidatableResponse;
import manon.model.user.form.UserLogin;
import manon.model.user.form.UserPasswordUpdateForm;
import manon.model.user.form.UserUpdateForm;
import manon.util.basetest.AbstractMockIT;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static manon.document.user.UserEntity.Validation.EMAIL_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.NICKNAME_PATTERN_ERRMSG;
import static manon.document.user.UserEntity.Validation.NICKNAME_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.PASSWORD_SIZE_ERRMSG;
import static manon.document.user.UserEntity.Validation.USERNAME_PATTERN_ERRMSG;
import static manon.document.user.UserEntity.Validation.USERNAME_SIZE_ERRMSG;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

class UserWSValidIT extends AbstractMockIT {

    Object[][] dataProviderShouldValidateRegister() {
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
            {SC_BAD_REQUEST, veryLongString("U"), "123456", new String[]{USERNAME_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", null, new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", "", new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", "1", new String[]{PASSWORD_SIZE_ERRMSG}},
            {SC_BAD_REQUEST, "USERNAME", veryLongString("1"), new String[]{PASSWORD_SIZE_ERRMSG}}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateRegister")
    void shouldValidateRegister(int statusCode, String username, String pwd, String[] errMsg) {
        ValidatableResponse response = whenAnonymous().getSpec()
            .body(UserLogin.builder().username(username).password(pwd).build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(VALIDATION_ERRORS_MSG, Matchers.hasSize(errMsg.length),
                VALIDATION_ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }

    Object[][] dataProviderShouldValidateUpdate() {
        return new Object[][]{
            {SC_OK, null, null, null},
            {SC_OK, "", "", null},
            {SC_OK, "", "valid@email.com", null},
            {SC_OK, "a valid nickname", "", null},
            {SC_OK, "a valid nickname", "valid@email.com", null},
            {SC_BAD_REQUEST, "", veryLongString("averylongemail") + "@test.com", EMAIL_SIZE_ERRMSG},
            {SC_BAD_REQUEST, "anickname!!!", "", NICKNAME_PATTERN_ERRMSG},
            {SC_BAD_REQUEST, veryLongString("averylongnickname"), "", NICKNAME_SIZE_ERRMSG}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateUpdate")
    void shouldValidateUpdate(int statusCode, String nickname, String email, String errMsg) {
        ValidatableResponse response = whenP1().getSpec()
            .body(UserUpdateForm.builder().nickname(nickname).email(email).build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(VALIDATION_ERRORS_MSG, Matchers.hasSize(1),
                VALIDATION_ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }

    Object[][] dataProviderShouldValidateUpdatePassword() {
        return new Object[][]{
            {SC_OK, pwd(1), "a new valid password", null},
            {SC_BAD_REQUEST, pwd(1), "", PASSWORD_SIZE_ERRMSG},
            {SC_BAD_REQUEST, pwd(1), null, PASSWORD_SIZE_ERRMSG},
            {SC_BAD_REQUEST, pwd(1), veryLongString("averylongpassword"), PASSWORD_SIZE_ERRMSG}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldValidateUpdatePassword")
    void shouldValidateUpdatePassword(int statusCode, String oldPwd, String newPwd, String errMsg) {
        ValidatableResponse response = whenP1().getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword(oldPwd).newPassword(newPwd).build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(statusCode);
        if (SC_BAD_REQUEST == statusCode) {
            response.body(VALIDATION_ERRORS_MSG, Matchers.hasSize(1),
                VALIDATION_ERRORS_MSG, Matchers.containsInAnyOrder(errMsg));
        }
    }
}
