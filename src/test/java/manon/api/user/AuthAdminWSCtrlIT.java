package manon.api.user;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;

public class AuthAdminWSCtrlIT extends AbstractMockIT {

    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyRemoveAllExpiredTokens(Rs rs, Integer status) {
        rs.getSpec()
            .contentType(JSON)
            .delete(API_USER_ADMIN + "/auth/expired/all")
            .then()
            .statusCode(status);
        verify(authAdminWS, status).removeAllExpiredTokens();
    }
}
