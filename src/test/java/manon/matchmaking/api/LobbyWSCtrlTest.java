package manon.matchmaking.api;

import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static manon.matchmaking.LobbyLeagueEnum.REGULAR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class LobbyWSCtrlTest extends MockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyGetStatus(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .get(API_LOBBY + "/status")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).getStatus(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyQuit(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .put(API_LOBBY + "/quit")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).quit(any());
    }
    
    // Lobby solo
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyEnter(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .pathParam("league", REGULAR)
                .put(API_LOBBY + "/enter/{league}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).enter(any(), eq(REGULAR));
    }
    
    // Lobby team
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED_201)
    public void shouldVerifyCreateTeamAndEnter(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .pathParam("league", REGULAR)
                .post(API_LOBBY + "/team/{league}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).createTeamAndEnter(any(), eq(REGULAR));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyInviteToTeam(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("userId", FAKE_ID)
                .put(API_LOBBY + "/invite/user/{userId}/team")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).inviteToTeam(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyGetTeamInvitations(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .get(API_LOBBY + "/team/invitations")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).getTeamInvitations(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyAcceptTeamInvitation(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("invitationId", FAKE_ID)
                .put(API_LOBBY + "/accept/team/invitation/{invitationId}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).acceptTeamInvitation(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyGetTeam(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .get(API_LOBBY + "/team")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).getTeam(any());
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyMarkTeamReady(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("ready", FAKE_BOOL)
                .put(API_LOBBY + "/team/ready/{ready}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).markTeamReady(any(), eq(FAKE_BOOL));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifySetTeamLeader(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("newLeaderUserId", FAKE_ID)
                .put(API_LOBBY + "/team/leader/{newLeaderUserId}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).setTeamLeader(any(), eq(FAKE_ID));
    }
}
