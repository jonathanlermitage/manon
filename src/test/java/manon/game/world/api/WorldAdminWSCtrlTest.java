package manon.game.world.api;

import manon.game.world.form.WorldRegistrationForm;
import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static org.mockito.ArgumentMatchers.any;

public class WorldAdminWSCtrlTest extends MockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_ADMIN_201)
    public void shouldVerifyRegister(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .body(WorldRegistrationForm.builder()
                        .nbPointsPerSector(1)
                        .nbSectorsVertical(1)
                        .nbSectorsHorizontal(1)
                        .sectorHeight(1)
                        .sectorWidth(1)
                        .name("name")
                        .build())
                .contentType(JSON)
                .post(API_WORLD_ADMIN)
                .then()
                .statusCode(status);
        verify(worldAdminWS, status).register(any(), any());
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyDelete(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("id", FAKE_ID)
                .delete(API_WORLD_ADMIN + "/{id}")
                .then()
                .statusCode(status);
        verify(worldAdminWS, status).delete(any(), any());
    }
}
