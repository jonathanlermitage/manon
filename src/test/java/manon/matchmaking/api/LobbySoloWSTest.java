package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import lombok.SneakyThrows;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class LobbySoloWSTest extends LobbyWSBaseTest {
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterRegularLobbyAndCheckStatus(Rs rs) {
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterCompetitiveLobbyAndCheckStatus(Rs rs) {
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.COMPETITIVE)
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyLeagueEnum.COMPETITIVE);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterManyTimesInAnyLobby(Rs rs) {
        for (int i = 0; i < 3; i++) {
            shouldEnterRegularLobbyAndCheckStatus(rs);
            shouldEnterCompetitiveLobbyAndCheckStatus(rs);
        }
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterAndQuitRegularLobbyAndCheckStatus(Rs rs) {
        shouldEnterRegularLobbyAndCheckStatus(rs);
        shouldQuitLobbyAndCheckStatus(rs);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterAndQuitCompetitiveLobbyAndCheckStatus(Rs rs) {
        shouldEnterCompetitiveLobbyAndCheckStatus(rs);
        shouldQuitLobbyAndCheckStatus(rs);
    }
    
    @Test
    public void shouldNotEnterLobby_anonymous() {
        whenAnonymous().getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @SneakyThrows
    private void checkStatus(Rs rs, LobbyLeagueEnum league) {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res, ProfileLobbyStatus.class);
        assertNotNull(profileLobbyStatus.getLobbySolo());
        assertNull(profileLobbyStatus.getLobbyTeam());
        assertEquals(profileLobbyStatus.getLobbySolo().getLeague(), league);
    }
}
