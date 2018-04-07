package manon.game.world.api;

import io.restassured.response.Response;
import manon.game.world.document.World;
import manon.game.world.err.WorldExistsException;
import manon.game.world.err.WorldNotFoundException;
import manon.game.world.form.WorldRegistrationForm;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static manon.app.config.ControllerAdviceBase.FIELD_ERRORS;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldAdminWSTest extends InitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Test(dataProvider = DP_ADMIN)
    public void shouldRegister(Rs rs) throws WorldNotFoundException {
        Response res = rs.getRequestSpecification()
                .body(WorldRegistrationForm.builder()
                        .name("WORLD_1")
                        .sectorWidth(1L)
                        .sectorHeight(1L)
                        .nbSectorsHorizontal(1)
                        .nbSectorsVertical(1)
                        .nbPointsPerSector(1)
                        .build())
                .contentType(JSON)
                .post(API_WORLD_ADMIN);
        res.then()
                .statusCode(SC_CREATED);
        World webWorld = readValue(res, World.class);
        World dbWorld = worldService.read(webWorld.getId());
        assertEquals(webWorld, dbWorld);
        assertEquals(dbWorld.getName(), "WORLD_1");
        assertThat(dbWorld.getPoints()).hasSize(1);
        assertThat(dbWorld.getSectors()).hasSize(1);
        assertEquals(dbWorld.getNbPoints(), 1);
        assertEquals(dbWorld.getNbSectors(), 1);
    }
    
    @Test(dataProvider = DP_ADMIN)
    public void shouldNotRegisterSameName(Rs rs) {
        for (int i = 0; i < 2; i++) {
            Response res = rs.getRequestSpecification()
                    .body(WorldRegistrationForm.builder()
                            .name("WORLD_1")
                            .sectorWidth(1L)
                            .sectorHeight(1L)
                            .nbSectorsHorizontal(1)
                            .nbSectorsVertical(1)
                            .nbPointsPerSector(1)
                            .build())
                    .contentType(JSON)
                    .post(API_WORLD_ADMIN);
            if (i == 0) {
                res.then()
                        .statusCode(SC_CREATED);
            } else {
                res.then()
                        .statusCode(SC_CONFLICT)
                        .body(FIELD_ERRORS, Matchers.equalTo(WorldExistsException.class.getSimpleName()));
            }
        }
    }
    
    @Test(dataProvider = DP_ADMIN)
    public void shouldDelete(Rs rs) throws WorldExistsException {
        String id = worldService.register(WorldRegistrationForm.builder()
                .name("WORLD_1")
                .sectorWidth(1L)
                .sectorHeight(1L)
                .nbSectorsHorizontal(1)
                .nbSectorsVertical(1)
                .nbPointsPerSector(1)
                .build()).getId();
        rs.getRequestSpecification()
                .pathParam("id", id)
                .delete(API_WORLD_ADMIN + "/{id}")
                .then()
                .statusCode(SC_OK);
        assertEquals(worldService.count(), 0);
    }
    
    @Test(dataProvider = DP_ADMIN)
    public void shouldNotDeleteUnknown(Rs rs) {
        rs.getRequestSpecification()
                .pathParam("id", UNKNOWN_ID)
                .delete(API_WORLD_ADMIN + "/{id}")
                .then()
                .statusCode(SC_NOT_FOUND)
                .body(FIELD_ERRORS, Matchers.equalTo(WorldNotFoundException.class.getSimpleName()));
    }
}
