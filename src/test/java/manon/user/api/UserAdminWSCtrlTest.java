package manon.user.api;

import manon.util.basetest.AbstractMockBeforeClass;
import manon.util.web.Rs;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UserAdminWSCtrlTest extends AbstractMockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyFindAll(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_USER_ADMIN + "/all")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).findAll(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyActivate(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", FAKE_ID)
            .post(API_USER_ADMIN + "/{userId}/activate")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).activate(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyBan(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", FAKE_ID)
            .post(API_USER_ADMIN + "/{userId}/ban")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).ban(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifySuspend(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", FAKE_ID)
            .post(API_USER_ADMIN + "/{userId}/suspend")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).suspend(any(), eq(FAKE_ID));
    }
}
