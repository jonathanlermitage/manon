package manon.matchmaking.api;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import manon.matchmaking.model.LobbyLeague;
import manon.matchmaking.service.LobbyService;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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
    }
    
    @Test
    public void shouldQuitManyTimesLobbyAndCheckStatus() {
        for (int i = 0; i < 3; i++) {
            shouldQuitLobbyAndCheckStatus();
        }
    }
    
    @Test
    public void shouldEnterRegularLobbyAndCheckStatus() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/enter/" + LobbyLeague.REGULAR)
                .then().statusCode(SC_OK);
        checkStatus(whenP1(), LobbyLeague.REGULAR);
    }
    
    @Test
    public void shouldEnterCompetitiveLobbyAndCheckStatus() {
        whenP1().getRequestSpecification()
                .put(API_LOBBY + "/enter/" + LobbyLeague.COMPETITIVE)
                .then().statusCode(SC_OK);
        checkStatus(whenP1(), LobbyLeague.COMPETITIVE);
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
    private void checkStatus(Rs rs, LobbyLeague league) {
        Response res = rs.getRequestSpecification()
                .get(API_LOBBY + "/status");
        res.then()
                .contentType(JSON)
                .statusCode(SC_OK);
        manon.matchmaking.model.LobbyStatus lobbyStatus = readValue(res, manon.matchmaking.model.LobbyStatus.class);
        assertNotNull(lobbyStatus.getLobbySolo());
        assertEquals(lobbyStatus.getLobbySolo().getLeague(), league);
    }
}
