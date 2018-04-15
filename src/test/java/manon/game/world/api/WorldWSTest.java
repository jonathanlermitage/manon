package manon.game.world.api;

import io.restassured.response.Response;
import manon.game.world.document.World;
import manon.game.world.err.WorldExistsException;
import manon.game.world.form.WorldRegistrationForm;
import manon.game.world.model.WorldSummary;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import manon.util.web.WorldSummaryList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldWSTest extends InitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Test(dataProvider = DP_ADMIN)
    public void shouldReadWorldView(Rs rs) throws WorldExistsException {
        World expectedWorld = worldService.register(WorldRegistrationForm.builder()
                .name("WORLD_1")
                .sectorWidth(1L)
                .sectorHeight(1L)
                .nbSectorsHorizontal(1)
                .nbSectorsVertical(1)
                .nbPointsPerSector(1)
                .build());
        
        Response res = rs.getRequestSpecification()
                .pathParam("id", expectedWorld.getId())
                .get(API_WORLD + "/summary/{id}");
        res.then()
                .statusCode(SC_OK);
        WorldSummary webWorldSummary = readValue(res, WorldSummary.class);
        assertEquals(webWorldSummary.getId(), expectedWorld.getId());
        assertEquals(webWorldSummary.getName(), expectedWorld.getName());
        assertEquals(webWorldSummary.getNbPoints(), expectedWorld.getNbPoints());
        assertEquals(webWorldSummary.getNbSectors(), expectedWorld.getNbSectors());
    }
    
    @Test(dataProvider = DP_AUTHENTICATED)
    public void shouldNotReadUnknownWorldView(Rs rs) {
        rs.getRequestSpecification()
                .pathParam("id", UNKNOWN_ID)
                .get(API_WORLD + "/summary/{id}")
                .then()
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test(dataProvider = DP_AUTHENTICATED)
    public void shouldFindAllWorldViews(Rs rs) throws WorldExistsException {
        List<World> expectedWorlds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            expectedWorlds.add(worldService.register(WorldRegistrationForm.builder()
                    .name("WORLD_" + i)
                    .sectorWidth(1L)
                    .sectorHeight(1L)
                    .nbSectorsHorizontal(1)
                    .nbSectorsVertical(1)
                    .nbPointsPerSector(1)
                    .build()));
        }
        List<WorldSummary> expectedWorldSummaries = expectedWorlds.stream().map(world -> WorldSummary.builder()
                .id(world.getId())
                .name(world.getName())
                .nbSectors(world.getNbSectors())
                .nbPoints(world.getNbPoints())
                .build()).collect(Collectors.toList());
        
        Response res = rs.getRequestSpecification()
                .get(API_WORLD + "/summary/all");
        res.then()
                .statusCode(SC_OK);
        WorldSummaryList webWorldSummaries = readValue(res, WorldSummaryList.class);
        assertThat(webWorldSummaries).containsExactlyInAnyOrderElementsOf(expectedWorldSummaries);
    }
    
    @Test(dataProvider = DP_AUTHENTICATED)
    public void shouldFindZeroWorldViews(Rs rs) {
        Response res = rs.getRequestSpecification()
                .get(API_WORLD + "/summary/all");
        res.then()
                .statusCode(SC_OK);
        WorldSummaryList webWorldSummaries = readValue(res, WorldSummaryList.class);
        assertThat(webWorldSummaries).isEmpty();
    }
}
