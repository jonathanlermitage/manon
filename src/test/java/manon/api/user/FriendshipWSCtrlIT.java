package manon.api.user;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class FriendshipWSCtrlIT extends AbstractMockIT {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyAskFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/askfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).askFriendship(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyAcceptFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/acceptfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).acceptFriendshipRequest(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRejectFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/rejectfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).rejectFriendshipRequest(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyCancelFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/cancelfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).cancelFriendshipRequest(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRevokeFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/revokefriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).revokeFriendship(any(), eq(UNKNOWN_ID));
    }
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyGetFriends(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .contentType(JSON)
            .get(API_USER + "/friends")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).getFriends(any());
    }
}
