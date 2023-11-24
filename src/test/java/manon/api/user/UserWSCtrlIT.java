package manon.api.user;

import manon.model.user.form.UserLogin;
import manon.model.user.form.UserPasswordUpdateForm;
import manon.model.user.form.UserUpdateForm;
import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.mockito.ArgumentMatchers.any;

class UserWSCtrlIT extends AbstractMockIT {

    Object[] dataProviderShouldVerifyRegister() {
        return new Object[]{
            whenAdmin(),
            whenP1(),
            whenAnonymous()
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyRegister")
    void shouldVerifyRegister(Rs rs) {
        rs.getSpec()
            .body(UserLogin.builder().username("USERNAME").password("PASSWORD").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        verify(userWs, SC_CREATED).register(any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyDelete(Rs rs, Integer status) {
        rs.getSpec()
            .contentType(JSON)
            .delete(API_USER)
            .then()
            .statusCode(status);
        verify(userWs, status).delete(any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyRead(Rs rs, Integer status) {
        rs.getSpec()
            .get(API_USER)
            .then()
            .statusCode(status);
        verify(userWs, status).read(any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyReadAndIncludeUserSnapshots(Rs rs, Integer status) {
        rs.getSpec()
            .get(API_USER + "/include/usersnapshots")
            .then()
            .statusCode(status);
        verify(userWs, status).readAndIncludeUserSnapshots(any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyUpdateField(Rs rs, Integer status) {
        rs.getSpec()
            .body(UserUpdateForm.builder().build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(status);
        verify(userWs, status).update(any(), any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyUpdatePassword(Rs rs, Integer status) {
        rs.getSpec()
            .body(UserPasswordUpdateForm.builder().oldPassword("").newPassword("password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(status);
        verify(userWs, status).updatePassword(any(), any());
    }
}
