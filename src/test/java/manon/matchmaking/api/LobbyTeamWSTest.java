package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class LobbyTeamWSTest extends LobbyWSBaseTest {
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCreateTeamAndQuit(Rs rs) throws IOException {
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_LOBBY + "/team/" + LobbyLeagueEnum.REGULAR.name())
                .then().statusCode(SC_CREATED);
        checkStatus(rs, false, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS, dependsOnMethods = "shouldCreateTeamAndQuit")
    public void shouldCreateTeamAndQuitManyTimes(Rs rs) throws IOException {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndQuit(rs);
        }
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCreateTeamAndEnterSolo(Rs rs) throws IOException {
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_LOBBY + "/team/" + LobbyLeagueEnum.REGULAR.name())
                .then().statusCode(SC_CREATED);
        checkStatus(rs, false, LobbyLeagueEnum.REGULAR);
        
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR.name())
                .then().statusCode(SC_OK);
        checkStatus(rs, true, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS, dependsOnMethods = "shouldCreateTeamAndEnterSolo")
    public void shouldCreateTeamAndEnterSoloManyTimes(Rs rs) throws IOException {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndEnterSolo(rs);
        }
    }
    
    @Test
    public void shouldEnterSoloAndEnterTeam() {
    
    }
    
    @Test
    public void shouldEnterTeamAndEnterAnotherTeam() {
    
    }
    
    @Test(dependsOnMethods = "shouldEnterTeamAndEnterAnotherTeam")
    public void shouldEnterTeamAndEnterAnotherTeamManyTimes() {
        for (int i = 0; i < 3; i++) {
            shouldEnterTeamAndEnterAnotherTeam();
        }
    }
    
    @Test
    public void shouldMakeFullTeamAndIgnoreNewInvitations() {
    
    }
    
    @Test
    public void shouldNotAcceptInvitationToFullTeam() {
    
    }
    
    @Test
    public void shouldMakeFullTeamAndQuitAndReinvite() {
    
    }
    
    @Test
    public void shouldNotInviteHimself() {
    
    }
    
    @Test
    public void shouldNotAcceptInvitationForSomeoneElse() {
    
    }
    
    @Test
    public void shouldNotCancelInvitationForSomeoneElse() {
    
    }
    
    @Test
    public void shouldInviteRegular() {
    
    }
    
    @Test(dependsOnMethods = {"shouldInviteRegular"})
    public void shouldInviteRegularAndCancel() {
    
    }
    
    @Test
    public void shouldInviteCompetitive() {
    
    }
    
    @Test(dependsOnMethods = {"shouldInviteCompetitive"})
    public void shouldInviteCompetitiveAndCancel() {
    
    }
    
    @Test(dependsOnMethods = {"shouldInviteRegularAndCancel", "shouldInviteCompetitiveAndCancel"})
    public void shouldInviteAndCancelManyTimes() {
        for (int i = 0; i < 3; i++) {
            shouldInviteRegularAndCancel();
            shouldInviteCompetitiveAndCancel();
        }
    }
    
    @Test
    public void shouldInviteRegularAndAcceptAndIgnoreCancel() {
    
    }
    
    @Test
    public void shouldInviteCompetitiveAndAcceptAndIgnoreCancel() {
    
    }
    
    @Test(dependsOnMethods = {"shouldInviteRegularAndAcceptAndIgnoreCancel", "shouldInviteCompetitiveAndAcceptAndIgnoreCancel"})
    public void shouldInviteAndAcceptAndIgnoreCancelManyTimes() {
        for (int i = 0; i < 3; i++) {
            shouldInviteRegularAndAcceptAndIgnoreCancel();
            shouldInviteCompetitiveAndAcceptAndIgnoreCancel();
        }
    }
    
    private void checkStatus(Rs rs, boolean soloOtherwiseTeam, LobbyLeagueEnum league)
            throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res.asString(), ProfileLobbyStatus.class);
        if (soloOtherwiseTeam) {
            assertNotNull(profileLobbyStatus.getLobbySolo());
            assertNull(profileLobbyStatus.getLobbyTeam());
            assertEquals(profileLobbyStatus.getLobbySolo().getLeague(), league);
        } else {
            assertNull(profileLobbyStatus.getLobbySolo());
            assertNotNull(profileLobbyStatus.getLobbyTeam());
            assertEquals(profileLobbyStatus.getLobbyTeam().getLeague(), league);
        }
    }
}
