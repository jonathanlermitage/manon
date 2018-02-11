package manon.matchmaking.api;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.service.LobbyService;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import manon.util.web.TeamInvitationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.http.ContentType.JSON;
import static manon.util.Tools.objId;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class LobbyWSTest extends InitBeforeTest {
    
    @Autowired
    private LobbyService lobbyService;
    
    @BeforeMethod(dependsOnMethods = "beforeMethod")
    public void initLobby() {
        lobbyService.flush();
    }
    
    @Override
    public int getNumberOfUsers() {
        return 7;
    }
    
    @Test
    public void shouldGetStatusInLobby() {
        Response res = whenP1().getRequestSpecification()
                .get(API_LOBBY + "/status");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        manon.matchmaking.model.LobbyStatus lobbyStatus = readValue(res, manon.matchmaking.model.LobbyStatus.class);
        assertEquals(lobbyStatus, manon.matchmaking.model.LobbyStatus.EMPTY);
    }
    
    @Test
    public void shouldQuitLobbyAndCheckStatus() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/quit")
                .then().statusCode(SC_OK);
        Response res = whenP1().getRequestSpecification()
                .get(API_LOBBY + "/status");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        manon.matchmaking.model.LobbyStatus lobbyStatus = readValue(res, manon.matchmaking.model.LobbyStatus.class);
        assertNull(lobbyStatus.getLobbySolo());
        assertNull(lobbyStatus.getLobbyTeam());
    }
    
    @Test
    public void shouldQuitManyTimesLobbyAndCheckStatus() {
        for (int i = 0; i < 3; i++) {
            shouldQuitLobbyAndCheckStatus();
        }
    }
    
    // Lobby solo
    
    @Test
    public void shouldEnterRegularLobbyAndCheckStatus() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_OK);
        checkStatus(whenP1(), LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldEnterCompetitiveLobbyAndCheckStatus() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/enter/" + LobbyLeagueEnum.COMPETITIVE)
                .then().statusCode(SC_OK);
        checkStatus(whenP1(), LobbyLeagueEnum.COMPETITIVE);
    }
    
    @Test
    public void shouldEnterManyTimesInAnyLobby() {
        for (int i = 0; i < 3; i++) {
            shouldEnterRegularLobbyAndCheckStatus();
            shouldEnterCompetitiveLobbyAndCheckStatus();
        }
    }
    
    @Test
    public void shouldEnterAndQuitRegularLobbyAndCheckStatus() {
        shouldEnterRegularLobbyAndCheckStatus();
        shouldQuitLobbyAndCheckStatus();
    }
    
    @Test
    public void shouldEnterAndQuitCompetitiveLobbyAndCheckStatus() {
        shouldEnterCompetitiveLobbyAndCheckStatus();
        shouldQuitLobbyAndCheckStatus();
    }
    
    @SneakyThrows
    private void checkStatus(Rs rs, LobbyLeagueEnum league) {
        Response res = rs.getRequestSpecification()
                .get(API_LOBBY + "/status");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        manon.matchmaking.model.LobbyStatus lobbyStatus = readValue(res, manon.matchmaking.model.LobbyStatus.class);
        assertNotNull(lobbyStatus.getLobbySolo());
        assertNull(lobbyStatus.getLobbyTeam());
        assertEquals(lobbyStatus.getLobbySolo().getLeague(), league);
    }
    
    // Lobby team
    
    @Test
    public void shouldCreateTeamAndQuit() {
        createTeamAlone(whenP1());
        checkStatus(whenP1(), LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dependsOnMethods = "shouldCreateTeamAndQuit")
    public void shouldCreateTeamAndQuitManyTimes() {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndQuit();
        }
    }
    
    @Test
    public void shouldCreateTeamAndEnterSolo() {
        createTeamAlone(whenP1());
        checkStatus(whenP1(), LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/enter/" + LobbyLeagueEnum.REGULAR)
                .then().statusCode(SC_OK);
        checkStatus(whenP1(), LobbyWSTest.LobbyStatus.SOLO, LobbyLeagueEnum.REGULAR);
    }
    
    @Test(dependsOnMethods = "shouldCreateTeamAndEnterSolo")
    public void shouldCreateTeamAndEnterSoloManyTimes() {
        for (int i = 0; i < 3; i++) {
            shouldCreateTeamAndEnterSolo();
        }
    }
    
    @Test
    public void shouldInvite() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeamAlone(rs);
        String user2Id = userService.readByUsername(rs2.getUsername()).getId();
        rs.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + user2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatusOut(rs2);
    }
    
    @Test
    public void shouldNotInviteHimself() {
        Rs rs = whenP1();
        createTeamAlone(rs);
        String userId = userService.readByUsername(rs.getUsername()).getId();
        rs.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + userId + "/team")
                .then().statusCode(SC_CONFLICT);
        checkStatus(rs, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldNotInviteUnknownUser() {
        Rs rs = whenP1();
        createTeamAlone(rs);
        rs.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + objId() + "/team")
                .then().statusCode(SC_NOT_FOUND);
        checkStatus(rs, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldAcceptInvitationCheckTeamAndCheckPendingInvitations() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeamAlone(rs);
        String userId = userService.readByUsername(rs.getUsername()).getId();
        String user2Id = userService.readByUsername(rs2.getUsername()).getId();
        rs.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + user2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rs, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatusOut(rs2);
        
        Response resInvitations = rs2.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        resInvitations.then().statusCode(SC_OK);
        TeamInvitationList invitations = readValue(resInvitations, TeamInvitationList.class);
        assertThat(invitations).hasSize(1);
        
        Response resAccept = rs2.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + invitations.get(0).getId());
        resAccept.then().statusCode(SC_OK);
        
        resInvitations = rs2.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        resInvitations.then().statusCode(SC_OK);
        invitations = readValue(resInvitations, TeamInvitationList.class);
        assertThat(invitations).hasSize(0);
        LobbyTeam team = readValue(resAccept, LobbyTeam.class);
        assertThat(team.getUserIds()).hasSize(2).contains(userId).contains(user2Id);
        checkStatus(rs, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatus(rs2, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
    }
    
    @Test
    public void shouldNotAcceptInvitationToFullTeamUnlessAMemberLeaves() {
        Rs rs3 = whenP3();
        String p1TeamId = createTeamAlone(whenP1()).getId();
        
        // P1 invites P2 to P7
        IntStream.rangeClosed(2, 7).parallel().forEach(s -> {
            String userId = userService.readByUsername(whenPX(s).getUsername()).getId();
            whenP1().getRequestSpecification()
                    .contentType(JSON)
                    .put(API_LOBBY + "/invite/user/" + userId + "/team")
                    .then().statusCode(SC_OK);
        });
        
        // P2 to P6 accept invitation
        IntStream.rangeClosed(2, 6).parallel().forEach(s -> {
            Rs rs = whenPX(s);
            Response resInvitations = rs.getRequestSpecification()
                    .contentType(JSON)
                    .get(API_LOBBY + "/team/invitations");
            resInvitations.then().statusCode(SC_OK);
            TeamInvitationList invitations = readValue(resInvitations, TeamInvitationList.class);
            assertThat(invitations).hasSize(1);
            rs.getRequestSpecification()
                    .contentType(JSON)
                    .put(API_LOBBY + "/accept/team/invitation/" + invitations.get(0).getId())
                    .then().statusCode(SC_OK);
        });
        
        // P7 can't enter since team is full
        Rs rs7 = whenPX(7);
        Response res7Invitations = rs7.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        res7Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations7 = readValue(res7Invitations, TeamInvitationList.class);
        assertThat(invitations7).hasSize(1);
        rs7.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + invitations7.get(0).getId())
                .then().statusCode(SC_CONFLICT);
        
        // P3 leaves team, than P7 can now enter
        Response res3Invitations = rs3.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/quit");
        res3Invitations.then().statusCode(SC_OK);
        rs7.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + invitations7.get(0).getId())
                .then().statusCode(SC_OK);
        
        // check that P1 P2 P4 P5 P6 P7 are on the same team, and P3 is out
        Stream.of(whenP1(), whenP2(), whenPX(4), whenPX(5), whenPX(6), whenPX(7)).parallel().forEach(rs -> {
            Response res = rs.getRequestSpecification()
                    .contentType(JSON)
                    .get(API_LOBBY + "/team");
            res.then().statusCode(SC_OK);
            LobbyTeam actualTeam = readValue(res, LobbyTeam.class);
            assertEquals(actualTeam.getId(), p1TeamId);
        });
        rs3.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team")
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotAcceptUnknownInvitation() {
        whenP1().getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + objId())
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotAcceptOtherUserInvitation() {
        Rs rs2 = whenP2();
        Rs rs3 = whenP3();
        createTeamAlone(whenP1());
        String user2Id = userService.readByUsername(rs2.getUsername()).getId();
        String user3Id = userService.readByUsername(rs3.getUsername()).getId();
        Stream.of(user2Id, user3Id).parallel().forEach(s -> whenP1().getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + s + "/team")
                .then().statusCode(SC_OK));
        
        Response res2Invitations = rs2.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        res2Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations2 = readValue(res2Invitations, TeamInvitationList.class);
        assertThat(invitations2).hasSize(1);
        
        Response res3Invitations = rs3.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        res3Invitations.then().statusCode(SC_OK);
        TeamInvitationList invitations3 = readValue(res3Invitations, TeamInvitationList.class);
        assertThat(invitations3).hasSize(1);
        rs3.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + invitations2.get(0).getId())
                .then().statusCode(SC_NOT_FOUND);
    }
    
    @Test(dataProvider = DP_TRUEFALSE)
    public void shouldSetTeamReadyWhenAlone(boolean ready) {
        Rs rs = whenP1();
        createTeamAlone(rs);
        Response res = rs.getRequestSpecification()
                .put(API_LOBBY + "/team/ready/" + ready);
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res, LobbyTeam.class);
        assertTrue(team.isReady() == ready);
    }
    
    @Test(dataProvider = DP_TRUEFALSE)
    public void shouldSetTeamReadyWhenNotAlone(boolean ready) {
        Rs rs = whenP1();
        createTeamOfTwo(rs, whenP2());
        rs.getRequestSpecification()
                .put(API_LOBBY + "/team/ready/" + ready).then()
                .contentType(JSON)
                .statusCode(SC_OK);
    }
    
    @Test
    public void shouldNotSetTeamReadyWhenNotLeader() {
        Rs rs2 = whenP2();
        createTeamOfTwo(whenP1(), rs2);
        rs2.getRequestSpecification()
                .put(API_LOBBY + "/team/ready/true").then()
                .contentType(JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test
    public void shouldNotSetTeamReadyWhenNotInTeam() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/team/ready/true").then()
                .contentType(JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldSetTeamLeader() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String user2Id = userId(2);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + user2Id);
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res, LobbyTeam.class);
        assertEquals(team.getLeader(), user2Id);
    }
    
    @Test
    public void shouldSetTeamLeaderHimselfWhenLeader() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String user1Id = userId(1);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + user1Id);
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res, LobbyTeam.class);
        assertEquals(team.getLeader(), user1Id);
    }
    
    @Test(dependsOnMethods = "shouldSetTeamLeader")
    public void shouldSetTeamLeaderAndSetAnotherAgain() {
        shouldSetTeamLeader();
        String user1Id = userId(2);
        Response res = whenP2().getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + user1Id);
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res, LobbyTeam.class);
        assertEquals(team.getLeader(), user1Id);
    }
    
    @Test
    public void shouldElectNewTeamLeaderWhenOriginalLeaves() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        createTeamOfTwo(rs, rs2);
        rs.getRequestSpecification()
                .put(API_LOBBY + "/quit")
                .then()
                .statusCode(SC_OK);
        
        rs.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team").then()
                .contentType(JSON)
                .statusCode(SC_NOT_FOUND);
        
        Response res = rs2.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam actualTeam = readValue(res, LobbyTeam.class);
        assertEquals(actualTeam.getLeader(), userId(2));
    }
    
    @Test
    public void shouldNotSetTeamLeaderWhenNotLeader() {
        Rs rs2 = whenP2();
        createTeamOfTwo(whenP1(), rs2);
        rs2.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + userId(2)).then()
                .contentType(JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test
    public void shouldNotSetTeamLeaderFromNoTeam() {
        Rs rs = whenP1();
        createTeamOfTwo(rs, whenP2());
        rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + userId(3)).then()
                .contentType(JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test
    public void shouldNotSetTeamLeaderFromOtherTeam() {
        Rs rs = whenP1();
        createTeamAlone(whenP3());
        createTeamOfTwo(rs, whenP2());
        rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + userId(3)).then()
                .contentType(JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    @Test(dependsOnMethods = {"shouldSetTeamLeader", "shouldNotSetTeamLeaderWhenNotLeader"})
    public void shouldNotSetTeamLeaderWhenNotLeaderAnymore() {
        Rs rs = whenP1();
        Rs rs2 = whenP2();
        String user2Id = userId(2);
        createTeamOfTwo(rs, rs2);
        Response res = rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + user2Id);
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        LobbyTeam team = readValue(res, LobbyTeam.class);
        assertEquals(team.getLeader(), user2Id);
        
        rs.getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + userId(2)).then()
                .contentType(JSON)
                .statusCode(SC_CONFLICT);
    }
    
    @Test
    public void shouldNotSetTeamLeaderWhenNotInTeam() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/team/leader/" + userId(1)).then()
                .contentType(JSON)
                .statusCode(SC_NOT_FOUND);
    }
    
    private void checkStatusOut(Rs rs) {
        checkStatus(rs, LobbyWSTest.LobbyStatus.OUT, null);
    }
    
    @SneakyThrows
    private void checkStatus(Rs rs, LobbyStatus lobbyStatus, LobbyLeagueEnum league) {
        Response res = rs.getRequestSpecification()
                .get(API_LOBBY + "/status");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        manon.matchmaking.model.LobbyStatus userLobbyStatus = readValue(res, manon.matchmaking.model.LobbyStatus.class);
        switch (lobbyStatus) {
            case OUT:
                assertNull(userLobbyStatus.getLobbySolo());
                assertNull(userLobbyStatus.getLobbyTeam());
                break;
            case SOLO:
                assertNotNull(userLobbyStatus.getLobbySolo());
                assertNull(userLobbyStatus.getLobbyTeam());
                assertEquals(userLobbyStatus.getLobbySolo().getLeague(), league);
                break;
            case TEAM:
                assertNull(userLobbyStatus.getLobbySolo());
                assertNotNull(userLobbyStatus.getLobbyTeam());
                assertEquals(userLobbyStatus.getLobbyTeam().getLeague(), league);
                break;
            default:
                throw new Exception("bad lobbyStatus: " + lobbyStatus);
        }
    }
    
    private LobbyTeam createTeamAlone(Rs rs) {
        Response res = rs.getRequestSpecification()
                .post(API_LOBBY + "/team/" + LobbyLeagueEnum.REGULAR);
        res.then().statusCode(SC_CREATED);
        return readValue(res, LobbyTeam.class);
    }
    
    private void createTeamOfTwo(Rs rsLeader, Rs rs2) {
        createTeamAlone(rsLeader);
        String user2Id = userService.readByUsername(rs2.getUsername()).getId();
        rsLeader.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/invite/user/" + user2Id + "/team")
                .then().statusCode(SC_OK);
        checkStatus(rsLeader, LobbyWSTest.LobbyStatus.TEAM, LobbyLeagueEnum.REGULAR);
        checkStatusOut(rs2);
        
        Response resInvitations = rs2.getRequestSpecification()
                .contentType(JSON)
                .get(API_LOBBY + "/team/invitations");
        resInvitations.then().statusCode(SC_OK);
        TeamInvitationList invitations = readValue(resInvitations, TeamInvitationList.class);
        assertThat(invitations).hasSize(1);
        
        Response resAccept = rs2.getRequestSpecification()
                .contentType(JSON)
                .put(API_LOBBY + "/accept/team/invitation/" + invitations.get(0).getId());
        resAccept.then().statusCode(SC_OK);
    }
    
    private enum LobbyStatus {
        OUT,
        SOLO,
        TEAM
    }
}
