package manon.app.info.api;

import manon.util.basetest.AbstractMockBeforeClass;
import manon.util.web.Rs;
import org.testng.annotations.Test;

public class InfoWSCtrlTest extends AbstractMockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyGetAppVersion(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .get(API_SYS + "/info/app-version")
            .then()
            .statusCode(status);
        verify(infoWS, status).getAppVersion();
    }
}
