package manon.api.user;

import manon.util.basetest.AbstractMockTest;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UserAdminWSCtrlTest extends AbstractMockTest {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyFindAll(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_USER_ADMIN + "/all")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).findAll(any(), any());
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyActivate(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", UNKNOWN_ID)
            .post(API_USER_ADMIN + "/{userId}/activate")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).activate(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyBan(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", UNKNOWN_ID)
            .post(API_USER_ADMIN + "/{userId}/ban")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).ban(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifySuspend(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("userId", UNKNOWN_ID)
            .post(API_USER_ADMIN + "/{userId}/suspend")
            .then()
            .statusCode(status);
        verify(userAdminWS, status).suspend(any(), eq(UNKNOWN_ID));
    }
}
