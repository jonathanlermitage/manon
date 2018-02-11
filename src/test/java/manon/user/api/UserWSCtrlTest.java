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
    public Object[][] dataProviderRegister() {
        return new Object[][]{
                {whenAdmin(), SC_CREATED},
                {whenP1(), SC_CREATED},
                {whenAnonymous(), SC_CREATED}
        };
    }
    
    @Test(dataProvider = "dataProviderRegister")
    public void shouldVerifyRegister(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new RegistrationForm())
                .contentType(JSON)
                .post(API_USER)
                .then()
                .statusCode(status);
        verify(userWs, status).register(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyDelete(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new RegistrationForm())
                .contentType(JSON)
                .delete(API_USER)
                .then()
                .statusCode(status);
        verify(userWs, status).delete(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRead(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new RegistrationForm())
                .get(API_USER)
                .then()
                .statusCode(status);
        verify(userWs, status).read(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyReadVersion(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new RegistrationForm())
                .get(API_USER + "/version")
                .then()
                .statusCode(status);
        verify(userWs, status).readVersion(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdateField(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new UserUpdateForm())
                .contentType(JSON)
                .put(API_USER + "/field")
                .then()
                .statusCode(status);
        verify(userWs, status).updateField(any(), any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyUpdatePassword(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(new UserPasswordUpdateForm())
                .contentType(JSON)
                .put(API_USER + "/password")
                .then()
                .statusCode(status);
        verify(userWs, status).updatePassword(any(), any(), any());
    }
}
