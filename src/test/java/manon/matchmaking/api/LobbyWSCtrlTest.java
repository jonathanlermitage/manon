package manon.matchmaking.api;

import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static manon.matchmaking.model.LobbyLeague.REGULAR;
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
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyEnter(Rs rs, Integer status) {
        rs.getRequestSpecification()
                .pathParam("league", REGULAR)
                .put(API_LOBBY + "/enter/{league}")
                .then()
                .statusCode(status);
        verify(lobbyWS, status).enter(any(), eq(REGULAR));
    }
}
