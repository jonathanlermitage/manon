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

import java.util.stream.IntStream;
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
import static org.testng.Assert.assertTrue;

public class LobbyTeamWSTest extends LobbyWSBaseTest {
    
    @Override
    public int getNumberOfProfiles() {
        return 7;
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldCreateTeamAndQuit(Rs rs) {
        createTeamAlone(rs);
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
        createTeamAlone(rs);
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
        createTeamAlone(rs);
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
        createTeamAlone(rs);
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
        createTeamAlone(rs);
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
        createTeamAlone(rs);
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
        Rs rs3 = whenP3();
        String p1TeamId = createTeamAlone(whenP1()).getId();
        
        // P1 invites P2 to P7
        IntStream.rangeClosed(2, 7).parallel().forEach(s -> {
            String profileId = userService.readByUsername(whenPX(s).getUsername()).getProfileId();
            whenP1().getRequestSpecification()
                    .contentType(ContentType.JSON)
                    .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + profileId + "/team")
                    .then().statusCode(SC_OK);
        });
        
        // P2 to P6 accept invitation
        IntStream.rangeClosed(2, 6).parallel().forEach(s -> {
            Rs rs = whenPX(s);
            Response resInvitations = rs.getRequestSpecification()
                    .contentType(ContentType.JSON)
                    .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
            resInvitations.then().statusCode(SC_OK);
            TeamInvitationList invitations = readValue(resInvitations.asString(), TeamInvitationList.class);
            assertThat(invitations).hasSize(1);
            rs.getRequestSpecification()
                    .contentType(ContentType.JSON)
                    .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations.get(0).getId())
                    .then().statusCode(SC_OK);
        });
        
        // P7 can't enter since team is full
        Rs rs7 = whenPX(7);
        Response res7Invitations = rs7.getRequestSpecification()
                .contentType(ContentType.JSON)
                .get(getApiV1() + TEST_API_LOBBY + "/team/invitations");
        res7Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations7 = readValue(res7Invitations.asString(), TeamInvitationList.class);
        assertThat(invitations7).hasSize(1);
        rs7.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations7.get(0).getId())
                .then().statusCode(SC_CONFLICT);
        
        // P3 leaves team, than P7 can now enter
        Response res3Invitations = rs3.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/quit");
        res3Invitations.then().statusCode(SC_OK);
        rs7.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/accept/team/invitation/" + invitations7.get(0).getId())
                .then().statusCode(SC_OK);
        
        // check that P1 P2 P4 P5 P6 P7 are on the same team, and P3 is out
        Stream.of(whenP1(), whenP2(), whenPX(4), whenPX(5), whenPX(6), whenPX(7)).parallel().forEach(rs -> {
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
        createTeamAlone(whenP1());
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
    
    @Test(dataProvider = DP_TRUEFALSE)
    public void shouldSetTeamReadyWhenAlone(boolean ready) {
        Rs rs = whenP1();
        createTeamAlone(rs);
        Response res = rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/ready/" + ready);
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res.asString(), LobbyTeam.class);
        assertTrue(team.isReady() == ready);
    }
    
    @Test(dataProvider = DP_TRUEFALSE)
    public void shouldSetTeamReadyWhenNotAlone(boolean ready) {
        Rs rs = whenP1();
        createTeamOfTwo(rs, whenP2());
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/ready/" + ready).then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
    }
    
    @Test
    public void shouldNotSetTeamReadyWhenNotLeader() {
        Rs rs2 = whenP2();
        createTeamOfTwo(whenP1(), rs2);
        rs2.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/ready/true").then()
                .contentType(ContentType.JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test
    public void shouldNotSetTeamReadyWhenNotInTeam() {
        whenP1().getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/ready/true").then()
                .contentType(ContentType.JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotSetTeamReady_anonymous() {
        whenAnonymous().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/team/ready/true")
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldSetTeamLeader() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String profile2Id = profileId(2);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profile2Id);
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res.asString(), LobbyTeam.class);
        assertEquals(team.getLeader(), profile2Id);
    }
    
    @Test
    public void shouldSetTeamLeaderHimselfWhenLeader() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String profile1Id = profileId(1);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profile1Id);
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res.asString(), LobbyTeam.class);
        assertEquals(team.getLeader(), profile1Id);
    }
    
    @Test(dependsOnMethods = "shouldSetTeamLeader")
    public void shouldSetTeamLeaderAndSetAnotherAgain() {
        shouldSetTeamLeader();
        String profile1Id = profileId(2);
        Response res = whenP2().getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profile1Id);
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res.asString(), LobbyTeam.class);
        assertEquals(team.getLeader(), profile1Id);
    }
    
    @Test
    public void shouldNotSetTeamLeaderWhenNotLeader() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeamOfTwo(rs, rs2);
        rs2.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profileId(2)).then()
                .contentType(ContentType.JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test(dependsOnMethods = {"shouldSetTeamLeader", "shouldNotSetTeamLeaderWhenNotLeader"})
    public void shouldNotSetTeamLeaderWhenNotLeaderAnymore() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String profile2Id = profileId(2);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profile2Id);
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res.asString(), LobbyTeam.class);
        assertEquals(team.getLeader(), profile2Id);
        
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profileId(2)).then()
                .contentType(ContentType.JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test
    public void shouldNotSetTeamLeaderWhenNotInTeam() {
        whenP1().getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + profileId(1)).then()
                .contentType(ContentType.JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotSetTeamLeader_anonymous() {
        whenAnonymous().getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/team/leader/" + objId())
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
    
    private LobbyTeam createTeamAlone(Rs rs) {
        Response res = rs.getRequestSpecification()
                .contentType(ContentType.JSON)
                .post(getApiV1() + TEST_API_LOBBY + "/team/" + LobbyLeagueEnum.REGULAR);
        res.then().statusCode(SC_CREATED);
        return readValue(res.asString(), LobbyTeam.class);
    }
    
    private void createTeamOfTwo(Rs rsLeader, Rs rs2) {
        createTeamAlone(rsLeader);
        String profile2Id = userService.readByUsername(rs2.getUsername()).getProfileId();
        rsLeader.getRequestSpecification()
                .contentType(ContentType.JSON)
                .put(getApiV1() + TEST_API_LOBBY + "/invite/profile/" + profile2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rsLeader, LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
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
    }
    
    private enum LobbyStatus {
        OUT,
        SOLO,
        TEAM
    }
}
