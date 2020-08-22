package manon.api.user;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.http.ContentType.JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FriendshipWSCtrlIT extends AbstractMockIT {

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyAskFriendship(Rs rs, Integer status) {
        rs.getSpec()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/askfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).askFriendship(any(), eq(UNKNOWN_ID));
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyAcceptFriendship(Rs rs, Integer status) {
        rs.getSpec()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/acceptfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).acceptFriendshipRequest(any(), eq(UNKNOWN_ID));
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyRejectFriendship(Rs rs, Integer status) {
        rs.getSpec()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/rejectfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).rejectFriendshipRequest(any(), eq(UNKNOWN_ID));
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyCancelFriendship(Rs rs, Integer status) {
        rs.getSpec()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/cancelfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).cancelFriendshipRequest(any(), eq(UNKNOWN_ID));
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyRevokeFriendship(Rs rs, Integer status) {
        rs.getSpec()
            .pathParam("id", UNKNOWN_ID)
            .post(API_USER + "/revokefriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).revokeFriendship(any(), eq(UNKNOWN_ID));
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyGetFriends(Rs rs, Integer status) {
        rs.getSpec()
            .contentType(JSON)
            .get(API_USER + "/friends")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).getFriends(any());
    }

    @ParameterizedTest
    @MethodSource(DP_ALLOW_AUTHENTICATED)
    void shouldVerifyGetFriendshipRequests(Rs rs, Integer status) {
        rs.getSpec()
            .contentType(JSON)
            .get(API_USER + "/friendshiprequests")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).getFriendshipRequests(any());
    }
}
