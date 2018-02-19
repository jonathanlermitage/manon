package manon.user.api;

import manon.user.form.UserPasswordUpdateForm;
import manon.user.form.UserUpdateForm;
import manon.user.registration.form.RegistrationForm;
import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.mockito.ArgumentMatchers.any;

public class UserWSCtrlTest extends MockBeforeClass {
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyRegister() {
        return new Object[][]{
                {whenAdmin()},
                {whenP1()},
                {whenAnonymous()}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyRegister")
    public void shouldVerifyRegister(Rs rs) throws Exception {
        rs.getRequestSpecification()
                .body(RegistrationForm.builder().username("USERNAME").password("PASSWORD").build())
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(SC_CREATED);
        verify(userWs, SC_CREATED).register(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyDelete(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .contentType(JSON)
                .delete(API_USER)
                .then()
                .statusCode(status);
        verify(userWs, status).delete(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRead(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .get(API_USER)
                .then()
                .statusCode(status);
        verify(userWs, status).read(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyReadVersion(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .get(API_USER + "/version")
                .then()
                .statusCode(status);
        verify(userWs, status).readVersion(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdateField(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .body(UserUpdateForm.builder().build())
                .contentType(JSON)
                .put(API_USER + "/field")
                .then()
                .statusCode(status);
        verify(userWs, status).update(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdatePassword(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .body(UserPasswordUpdateForm.builder().oldPassword("").newPassword("password").build())
                .contentType(JSON)
                .put(API_USER + "/password")
                .then()
                .statusCode(status);
        verify(userWs, status).updatePassword(any(), any());
    }
}
