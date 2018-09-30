package manon.user.api;

import manon.user.document.User;
import manon.user.err.FriendshipExistsException;
import manon.user.err.FriendshipRequestExistsException;
import manon.user.err.FriendshipRequestNotFoundException;
import manon.user.err.UserNotFoundException;
import manon.user.model.FriendshipEventCode;
import manon.util.basetest.InitBeforeClass;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static manon.app.config.ControllerAdviceBase.FIELD_ERRORS;
import static manon.app.config.ControllerAdviceBase.FIELD_MESSAGE;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class FriendshipWSTest extends InitBeforeClass {
    
    @Override
    public int getNumberOfUsers() {
        return 3;
    }
    
    @AfterMethod
    public void reinitFriendships() throws UserNotFoundException {
        for (String userId : new String[]{userId(1), userId(2), userId(3)}) {
            User user = userService.readOne(userId);
            user.getFriends().clear();
            user.getFriendshipRequestsTo().clear();
            user.getFriendshipRequestsFrom().clear();
            user.getFriendshipEvents().clear();
            userService.save(user);
        }
    }
    
    /** Ask many friendship requests and targets accept them. */
    @Test
    public void shouldAcceptFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(3))
            .then()
            .statusCode(SC_OK);
        User p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().contains(userId(2), userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        User p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //THEN P2 accepts
        whenP2().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(1))
            .then()
            .statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(userId(2));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().containsExactly(userId(1));
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //WHEN P1 sends friendship request again
        //THEN it should fail since friendship relation exists already
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipExistsException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(1), userId(2)));
        //WHEN P2 sends friendship to P1
        //THEN it should fail since friendship relation exists already
        whenP2().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(1))
            .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipExistsException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(2), userId(1)));
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST);
    }
    
    /** Ensure we don't store the same friendship request twice. */
    @Test
    public void shouldNotAskFriendshipTwice() throws Exception {
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipRequestExistsException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(1), userId(2)));
        whenP2().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(1))
            .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipRequestExistsException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(1), userId(2)));
        User p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(userId(2));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST);
    }
    
    /** Ask many friendship requests and targets reject them. */
    @Test
    public void shouldRejectFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(3))
            .then()
            .statusCode(SC_OK);
        User p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().contains(userId(2), userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        User p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //THEN P2 rejects request
        whenP2().getRequestSpecification()
            .post(API_USER + "/rejectfriendship/user/" + userId(1))
            .then()
            .statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //THEN P3 rejects request
        whenP3().getRequestSpecification()
            .post(API_USER + "/rejectfriendship/user/" + userId(1))
            .then()
            .statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST);
    }
    
    /** Create friendship requests, accept them, then revoke them. */
    @Test
    public void shouldRevokeFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3 and they accept
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2)).then().statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(3)).then().statusCode(SC_OK);
        whenP2().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(1)).then().statusCode(SC_OK);
        whenP3().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(1)).then().statusCode(SC_OK);
        
        //THEN P1 revokes friendship with P2
        whenP1().getRequestSpecification()
            .post(API_USER + "/revokefriendship/user/" + userId(2)).then().statusCode(SC_OK);
        User p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().containsExactly(userId(1));
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        //THEN P3 revokes non-existent friendship with P2, it should do nothing but registering event
        whenP3().getRequestSpecification()
            .post(API_USER + "/revokefriendship/user/" + userId(2)).then().statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().containsExactly(userId(1));
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        //THEN P3 revokes friendship with P1
        whenP3().getRequestSpecification()
            .post(API_USER + "/revokefriendship/user/" + userId(1)).then().statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST,
            FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST,
            FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST,
            FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST);
    }
    
    /** Create friendship requests, then cancel them. */
    @Test
    public void shouldCancelFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2)).then().statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(3)).then().statusCode(SC_OK);
        
        //THEN P1 cancels friendship request to P2
        whenP1().getRequestSpecification()
            .post(API_USER + "/cancelfriendship/user/" + userId(2)).then().statusCode(SC_OK);
        User p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        User p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isEmpty();
        User p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //THEN P3 cancels non-existent friendship request to P1, it should do nothing but registering event
        whenP3().getRequestSpecification()
            .post(API_USER + "/cancelfriendship/user/" + userId(1)).then().statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(userId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(userId(1));
        
        //THEN P1 cancels friendship request to P3
        whenP1().getRequestSpecification()
            .post(API_USER + "/cancelfriendship/user/" + userId(3)).then().statusCode(SC_OK);
        p1 = userService.readOne(userId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = userService.readOne(userId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = userService.readOne(userId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST,
            FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
            FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST,
            FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST,
            FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST);
    }
    
    @Test
    public void shouldNotAcceptSelfFriendshipRequest() {
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_OK);
        whenP1().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(1))
            .then()
            .statusCode(SC_NOT_FOUND)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipRequestNotFoundException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(1), userId(1)));
        whenP2().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_NOT_FOUND)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipRequestNotFoundException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(2), userId(2)));
    }
    
    @Test
    public void shouldNotAcceptUnknownFriendshipRequest() {
        whenP1().getRequestSpecification()
            .post(API_USER + "/askfriendship/user/" + userId(2))
            .then()
            .statusCode(SC_OK);
        whenP2().getRequestSpecification()
            .post(API_USER + "/acceptfriendship/user/" + userId(3))
            .then()
            .statusCode(SC_NOT_FOUND)
            .body(FIELD_ERRORS, Matchers.equalTo(FriendshipRequestNotFoundException.class.getSimpleName()),
                FIELD_MESSAGE, Matchers.hasSize(2),
                FIELD_MESSAGE, Matchers.contains(userId(3), userId(2)));
    }
    
    private void checkFriendshipEvents(User p, FriendshipEventCode... codes) {
        assertThat(p.getFriendshipEvents()).isNotNull().hasSize(codes.length);
        for (int i = 0; i < codes.length; i++) {
            assertEquals(p.getFriendshipEvents().get(i).getCode(), codes[i], "friendshipEvents[" + i + "]");
        }
    }
}
