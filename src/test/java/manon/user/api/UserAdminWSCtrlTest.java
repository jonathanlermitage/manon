package manon.user.api;

import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UserAdminWSCtrlTest extends MockBeforeClass {
    
    @MockBean
    private UserAdminWS ws;
    
    @BeforeMethod
    private void clearInvocations() {
        Mockito.clearInvocations(ws);
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyFindAll(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .get(API_USER_ADMIN + "/all")
                .then()
                .statusCode(status);
        verify(ws, status).findAll(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyActivate(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("userId", FAKE_ID)
                .post(API_USER_ADMIN + "/{userId}/activate")
                .then()
                .statusCode(status);
        verify(ws, status).activate(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyBan(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("userId", FAKE_ID)
                .post(API_USER_ADMIN + "/{userId}/ban")
                .then()
                .statusCode(status);
        verify(ws, status).ban(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifySuspend(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("userId", FAKE_ID)
                .post(API_USER_ADMIN + "/{userId}/suspend")
                .then()
                .statusCode(status);
        verify(ws, status).suspend(any(), eq(FAKE_ID));
    }
}
