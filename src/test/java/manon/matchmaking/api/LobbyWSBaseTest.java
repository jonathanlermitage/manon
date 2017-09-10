package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import lombok.SneakyThrows;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.repository.LobbySoloRepository;
import manon.matchmaking.repository.LobbyTeamRepository;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;

import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertNull;

public abstract class LobbyWSBaseTest extends InitBeforeTest {
    
    @Autowired
    private LobbySoloRepository lobbySoloRepository;
    @Autowired
    private LobbyTeamRepository lobbyTeamRepository;
    
    @BeforeMethod(dependsOnMethods = "beforeMethod")
    public void initLobby() throws Exception {
        lobbySoloRepository.deleteAll();
        lobbyTeamRepository.deleteAll();
    }
    
    @SneakyThrows
    public void shouldQuitLobbyAndCheckStatus(Rs rs) {
        rs.getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/quit")
                .then().statusCode(SC_OK);
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res.asString(), ProfileLobbyStatus.class);
        assertNull(profileLobbyStatus.getLobbySolo());
        assertNull(profileLobbyStatus.getLobbyTeam());
    }
}
