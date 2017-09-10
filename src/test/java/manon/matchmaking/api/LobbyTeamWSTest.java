package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import lombok.SneakyThrows;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.document.LobbyTeam;
import manon.util.basetest.Rs;
import manon.util.web.TeamInvitationList;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static manon.util.Tools.objId;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class LobbyTeamWSTest extends LobbyWSBaseTest {
    
    @Override
    public int getNumberOfProfiles() {
        return 4;
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCreateTeamAndQuit(Rs rs) {
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS, dependsOnMethods = "shouldCreateTeamAndQuit")
    public void shouldCreateTeamAndQuitManyTimes(Rs rs) {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndQuit(rs);
        }
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCreateTeamAndEnterSolo(Rs rs) {
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyStatus.SOLO, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dataProvider = DP_RS_USERS, dependsOnMethods = "shouldCreateTeamAndEnterSolo")
    public void shouldCreateTeamAndEnterSoloManyTimes(Rs rs) {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndEnterSolo(rs);
        }
    }
    
    @Test
    public void shouldNotCreateTeam_anonymous() {
        whenAnonymous().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/team/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldInvite() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        String profile2Id = userService.readByUsername(rs2.getUsername()).getProfileId();
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + profile2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatusOut(rs2);
    }
    
    @Test
    public void shouldNotInviteHimself() {
        Rs rs = whenP1();
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        String profileId = userService.readByUsername(rs.getUsername()).getProfileId();
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + profileId + "/team")
                .then().statusCode(SC_CONFLICT);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldNotInviteUnknownProfile() {
        Rs rs = whenP1();
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + objId() + "/team")
                .then().statusCode(SC_NOT_FOUND);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldNotInvite_anonymous() {
        whenAnonymous().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + objId() + "/team")
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldAcceptInvitationCheckTeamAndCheckPendingInvitations() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeam(rs, LobbyLeagueEnum.REGULAR);
        String profileId = userService.readByUsername(rs.getUsername()).getProfileId();
        String profile2Id = userService.readByUsername(rs2.getUsername()).getProfileId();
        rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + profile2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatusOut(rs2);
        
        Response resInvitations = rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        resInvitations.then().statusCode(SC_OK);
        TeamInvitationList invitations = readValue(resInvitations.asString(), TeamInvitationList.class);
        assertThat(invitations).hasSize(1);
        
        Response resAccept = rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations.get(0).getId());
        resAccept.then().statusCode(SC_OK);
        
        resInvitations = rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        resInvitations.then().statusCode(SC_OK);
        invitations = readValue(resInvitations.asString(), TeamInvitationList.class);
        assertThat(invitations).hasSize(0);
        LobbyTeam team = readValue(resAccept.asString(), LobbyTeam.class);
        assertThat(team.getProfileIds()).hasSize(2).contains(profileId).contains(profile2Id);
        checkStatus(rs, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatus(rs2, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldNotAcceptInvitationToFullTeamUnlessAMemberLeaves() {
        Rs rs2 = whenP2();
        Rs rs3 = whenP3();
        Rs rs4 = whenPX(4);
        String p1TeamId = createTeam(whenP1(), LobbyLeagueEnum.REGULAR).getId();
        String profile2Id = userService.readByUsername(rs2.getUsername()).getProfileId();
        String profile3Id = userService.readByUsername(rs3.getUsername()).getProfileId();
        String profile4Id = userService.readByUsername(rs4.getUsername()).getProfileId();
        Stream.of(profile2Id, profile3Id, profile4Id).parallel().forEach(s -> whenP1().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + s + "/team")
                .then().statusCode(SC_OK));
        
        Response res2Invitations = rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res2Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations2 = readValue(res2Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations2).hasSize(1);
        rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations2.get(0).getId())
                .then().statusCode(SC_OK);
        
        Response res3Invitations = rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res3Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations3 = readValue(res3Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations3).hasSize(1);
        rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations3.get(0).getId())
                .then().statusCode(SC_OK);
        
        // P4 can't enter since team is full
        Response res4Invitations = rs4.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res4Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations4 = readValue(res4Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations4).hasSize(1);
        rs4.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations4.get(0).getId())
                .then().statusCode(SC_CONFLICT);
        
        // P3 leaves team, than P4 can now enter
        rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/quit");
        res3Invitations.then().statusCode(SC_OK);
        rs4.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations4.get(0).getId())
                .then().statusCode(SC_OK);
        
        // check that P1 P2 P4 are on the same team, and P3 is out
        Stream.of(whenP1(), whenP2(), whenPX(4)).parallel().forEach(rs -> {
            Response res = rs.getRequestSpecification()
                    .contentType(ContentType.JSON)
                    .get(getApiV1() + TEST_API_LOBBY + "/team");
            res.then().statusCode(SC_OK);
            LobbyTeam actualTeam = readValue(res.asString(), LobbyTeam.class);
            assertEquals(actualTeam.getId(), p1TeamId);
        });
        rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team")
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotAcceptUnknownInvitation() {
        whenP1().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + objId())
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotAcceptOtherProfileInvitation() {
        Rs rs2 = whenP2();
        Rs rs3 = whenP3();
        createTeam(whenP1(), LobbyLeagueEnum.REGULAR);
        String profile2Id = userService.readByUsername(rs2.getUsername()).getProfileId();
        String profile3Id = userService.readByUsername(rs3.getUsername()).getProfileId();
        Stream.of(profile2Id, profile3Id).parallel().forEach(s -> whenP1().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + s + "/team")
                .then().statusCode(SC_OK));
        
        Response res2Invitations = rs2.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res2Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations2 = readValue(res2Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations2).hasSize(1);
        
        Response res3Invitations = rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res3Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations3 = readValue(res3Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations3).hasSize(1);
        rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations2.get(0).getId())
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotAcceptInvitation_anonymous() {
        whenAnonymous().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + objId())
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    private void checkStatusOut(Rs rs) {
        checkStatus(rs, LobbyStatus.OUT, null);
    }
    
    @SneakyThrows
    private void checkStatus(Rs rs, LobbyStatus lobbyStatus, LobbyLeagueEnum league) {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res.asString(), ProfileLobbyStatus.class);
        switch (lobbyStatus) {
            case OUT:
                assertNull(profileLobbyStatus.getLobbySolo());
                assertNull(profileLobbyStatus.getLobbyTeam());
                break;
            case SOLO:
                assertNotNull(profileLobbyStatus.getLobbySolo());
                assertNull(profileLobbyStatus.getLobbyTeam());
                assertEquals(profileLobbyStatus.getLobbySolo().getLeague(), league);
                break;
            case TEAM:
                assertNull(profileLobbyStatus.getLobbySolo());
                assertNotNull(profileLobbyStatus.getLobbyTeam());
                assertEquals(profileLobbyStatus.getLobbyTeam().getLeague(), league);
                break;
            default:
                throw new Exception("bad lobbyStatus: " + lobbyStatus);
        }
    }
    
    private LobbyTeam createTeam(Rs rs, LobbyLeagueEnum league) {
        Response res = rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_LOBBY + "/team/" + league);
        res.then().statusCode(SC_CREATED);
        return readValue(res.asString(), LobbyTeam.class);
    }
    
    private enum LobbyStatus {
        OUT,
        SOLO,
        TEAM
    }
}
