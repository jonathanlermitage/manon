package manon.api.app;

import manon.util.basetest.AbstractMockTest;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class InfoWSCtrlTest extends AbstractMockTest {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyGetAppVersion(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_SYS + "/info/app-version")
            .then()
            .statusCode(status);
        verify(infoWS, status).getAppVersion();
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ALL)
    public void shouldVerifyGetUp(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_SYS + "/info/up")
            .then()
            .statusCode(status);
        verify(infoWS, status).getUp();
    }
}
