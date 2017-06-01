package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class LobbySoloWSTest extends LobbyWSBaseTest {
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterRegularLobbyAndCheckStatus(Rs rs) throws IOException {
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR.name())
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterCompetitiveLobbyAndCheckStatus(Rs rs) throws IOException {
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.COMPETITIVE.name())
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyLeagueEnum.COMPETITIVE);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterManyTimesInAnyLobby(Rs rs) throws IOException {
        for (int i = 0; i < 3; i++) {
            shouldEnterRegularLobbyAndCheckStatus(rs);
            shouldEnterCompetitiveLobbyAndCheckStatus(rs);
        }
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterAndQuitRegularLobbyAndCheckStatus(Rs rs) throws IOException {
        shouldEnterRegularLobbyAndCheckStatus(rs);
        shouldQuitLobbyAndCheckStatus(rs);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldEnterAndQuitCompetitiveLobbyAndCheckStatus(Rs rs) throws IOException {
        shouldEnterCompetitiveLobbyAndCheckStatus(rs);
        shouldQuitLobbyAndCheckStatus(rs);
    }
    
    private void checkStatus(Rs rs, LobbyLeagueEnum league)
            throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res.asString(), ProfileLobbyStatus.class);
        assertNotNull(profileLobbyStatus.getLobbySolo());
        assertNull(profileLobbyStatus.getLobbyTeam());
        assertEquals(profileLobbyStatus.getLobbySolo().getLeague(), league);
    }
}
