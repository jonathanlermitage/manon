package manon.api.user;

import manon.err.user.PasswordNotMatchException;
import manon.model.user.form.RegistrationForm;
import manon.model.user.form.UserPasswordUpdateForm;
import manon.model.user.form.UserUpdateForm;
import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.mockito.ArgumentMatchers.any;

public class UserWSCtrlIT extends AbstractMockIT {
    
    public Object[] dataProviderShouldVerifyRegister() {
        return new Object[]{
            whenAdmin(),
            whenP1(),
            whenAnonymous()
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyRegister")
    public void shouldVerifyRegister(Rs rs) throws Exception {
        rs.getRequestSpecification()
            .body(RegistrationForm.builder().username("USERNAME").password("PASSWORD").build())
            .contentType(JSON)
            .post(API_USER)
            .then()
            .statusCode(SC_CREATED);
        verify(userWs, SC_CREATED).register(any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyDelete(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .contentType(JSON)
            .delete(API_USER)
            .then()
            .statusCode(status);
        verify(userWs, status).delete(any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRead(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .get(API_USER)
            .then()
            .statusCode(status);
        verify(userWs, status).read(any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyReadAndIncludeUserSnapshots(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .get(API_USER + "/include/usersnapshots")
            .then()
            .statusCode(status);
        verify(userWs, status).readAndIncludeUserSnapshots(any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyReadVersion(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .get(API_USER + "/version")
            .then()
            .statusCode(status);
        verify(userWs, status).readVersion(any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdateField(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .body(UserUpdateForm.builder().build())
            .contentType(JSON)
            .put(API_USER + "/field")
            .then()
            .statusCode(status);
        verify(userWs, status).update(any(), any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdatePassword(Rs rs, Integer status) throws PasswordNotMatchException {
        rs.getRequestSpecification()
            .body(UserPasswordUpdateForm.builder().oldPassword("").newPassword("password").build())
            .contentType(JSON)
            .put(API_USER + "/password")
            .then()
            .statusCode(status);
        verify(userWs, status).updatePassword(any(), any());
    }
}
