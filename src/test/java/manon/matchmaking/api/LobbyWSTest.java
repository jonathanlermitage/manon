package manon.matchmaking.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.matchmaking.ProfileLobbyStatus;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertEquals;

public class LobbyWSTest extends LobbyWSBaseTest {
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldGetStatusInLobby(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        ProfileLobbyStatus profileLobbyStatus = readValue(res.asString(), ProfileLobbyStatus.class);
        assertEquals(profileLobbyStatus, ProfileLobbyStatus.EMPTY);
    }
    
    @Test
    public void shouldNotGetStatusInLobby_anonymous() {
        whenAnonymous().getRequestSpecification()
                .get(getApiV1() + TEST_API_LOBBY + "/status")
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldQuitLobbyAndCheckStatus(Rs rs) throws IOException {
        super.shouldQuitLobbyAndCheckStatus(rs);
    }
    
    @Test
    public void shouldNotQuitLobby_anonymous() {
        whenAnonymous().getRequestSpecification()
                .put(getApiV1() + TEST_API_LOBBY + "/quit")
                .then().statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS)
    public void shouldQuitManyTimesLobbyAndCheckStatus(Rs rs) throws IOException {
        for (int i = 0; i < 3; i++) {
            shouldQuitLobbyAndCheckStatus(rs);
        }
    }
}
