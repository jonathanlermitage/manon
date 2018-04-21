package manon.game.world.api;

import manon.game.world.err.WorldNotFoundException;
import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;

public class WorldWSCtrlTest extends MockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyReadWorldView(Rs rs, Integer status) throws WorldNotFoundException {
        rs.getRequestSpecification()
                .pathParam("id", FAKE_ID)
                .get(API_WORLD + "/summary/{id}")
                .then()
                .statusCode(status);
        verify(worldWS, status).readWorldView(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyFindAllWorldViews(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .get(API_WORLD + "/summary/all")
                .then()
                .statusCode(status);
        verify(worldWS, status).findAllWorldViews(any());
    }
}
