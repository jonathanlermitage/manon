package manon.app.sys.api;

import manon.util.basetest.AbstractMockBeforeClass;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class InfoWSCtrlTest extends AbstractMockBeforeClass {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyGetAppVersion(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_SYS + "/info/app-version")
            .then()
            .statusCode(status);
        verify(infoWS, status).getAppVersion();
    }
}
