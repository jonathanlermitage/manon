package manon.user.api;

import manon.util.basetest.AbstractMockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class FriendshipWSCtrlTest extends AbstractMockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyAskFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", FAKE_ID)
            .post(API_USER + "/askfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).askFriendship(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyAcceptFriendship(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("id", FAKE_ID)
            .post(API_USER + "/acceptfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).acceptFriendshipRequest(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRejectFriendship(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .pathParam("id", FAKE_ID)
            .post(API_USER + "/rejectfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).rejectFriendshipRequest(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyCancelFriendship(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .pathParam("id", FAKE_ID)
            .post(API_USER + "/cancelfriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).cancelFriendshipRequest(any(), eq(FAKE_ID));
    }
    
    @Test(dataProvider = DP_ALLOW_AUTHENTICATED)
    public void shouldVerifyRevokeFriendship(Rs rs, Integer status) {
        rs.getRequestSpecification()
            .pathParam("id", FAKE_ID)
            .post(API_USER + "/revokefriendship/user/{id}")
            .then()
            .statusCode(status);
        verify(friendshipWS, status).revokeFriendship(any(), eq(FAKE_ID));
    }
}
