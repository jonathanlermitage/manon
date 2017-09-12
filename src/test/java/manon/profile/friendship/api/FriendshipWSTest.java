package manon.profile.friendship.api;

import manon.profile.ProfileNotFoundException;
import manon.profile.document.Profile;
import manon.profile.friendship.FriendshipEvent;
import manon.util.basetest.InitBeforeClass;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static manon.app.config.ControllerAdvice.FIELD_ERRORS;
import static manon.app.config.ControllerAdvice.FIELD_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class FriendshipWSTest extends InitBeforeClass {
    
    @Override
    public int getNumberOfProfiles() {
        return 3;
    }
    
    @AfterMethod
    public void reinitFriendships() throws ProfileNotFoundException {
        for (String profileId : new String[]{profileId(1), profileId(2), profileId(3)}) {
            Profile profile = profileService.readOne(profileId);
            profile.getFriends().clear();
            profile.getFriendshipRequestsTo().clear();
            profile.getFriendshipRequestsFrom().clear();
            profile.getFriendshipEvents().clear();
            profileService.save(profile);
        }
    }
    
    /** Ask many friendship requests and targets accept them. */
    @Test
    public void shouldAcceptFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(3))
                .then()
                .statusCode(HttpStatus.SC_OK);
        Profile p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().contains(profileId(2), profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        Profile p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //THEN P2 accepts
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(profileId(2));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().containsExactly(profileId(1));
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //WHEN P1 sends friendship request again
        //THEN it should fail since friendship relation exists already
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipExistsException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains(profileId(1), profileId(2)));
        //WHEN P2 sends friendship to P1
        //THEN it should fail since friendship relation exists already
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipExistsException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains(profileId(2), profileId(1)));
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_ACCEPTED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_ACCEPTED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST);
    }
    
    /** Ensure we don't store the same friendship request twice. */
    @Test
    public void shouldNotAskFriendshipTwice() throws Exception {
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipRequestExistsException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains(profileId(1), profileId(2)));
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipRequestExistsException"),
                        FIELD_MESSAGE, Matchers.hasSize(1),
                        FIELD_MESSAGE, Matchers.contains(profileId(1), profileId(2)));
        Profile p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(profileId(2));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST);
    }
    
    /** Ask many friendship requests and targets reject them. */
    @Test
    public void shouldRejectFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(3))
                .then()
                .statusCode(HttpStatus.SC_OK);
        Profile p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().contains(profileId(2), profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        Profile p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //THEN P2 rejects request
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/rejectfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //THEN P3 rejects request
        whenP3().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/rejectfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_REJECTED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_REJECTED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_REJECTED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_REJECTED_FRIEND_REQUEST);
    }
    
    /** Create friendship requests, accept them, then revoke them. */
    @Test
    public void shouldRevokeFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3 and they accept
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2)).then().statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(3)).then().statusCode(HttpStatus.SC_OK);
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(1)).then().statusCode(HttpStatus.SC_OK);
        whenP3().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(1)).then().statusCode(HttpStatus.SC_OK);
        
        //THEN P1 revokes friendship with P2
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/revokefriendship/profile/" + profileId(2)).then().statusCode(HttpStatus.SC_OK);
        Profile p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().containsExactly(profileId(1));
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        //THEN P3 revokes non-existent friendship with P2, it should do nothing but registering event
        whenP3().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/revokefriendship/profile/" + profileId(2)).then().statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().containsExactly(profileId(1));
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        //THEN P3 revokes friendship with P1
        whenP3().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/revokefriendship/profile/" + profileId(1)).then().statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_ACCEPTED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_ACCEPTED_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_REVOKED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_REVOKED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_ACCEPTED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_REVOKED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_REVOKED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_ACCEPTED_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_REVOKED_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_REVOKED_FRIEND_REQUEST);
    }
    
    /** Create friendship requests, then cancel them. */
    @Test
    public void shouldCancelFriendship() throws Exception {
        //WHEN P1 sends friendship requests to P2 and P3
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2)).then().statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(3)).then().statusCode(HttpStatus.SC_OK);
        
        //THEN P1 cancels friendship request to P2
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/cancelfriendship/profile/" + profileId(2)).then().statusCode(HttpStatus.SC_OK);
        Profile p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        Profile p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isEmpty();
        Profile p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //THEN P3 cancels non-existent friendship request to P1, it should do nothing but registering event
        whenP3().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/cancelfriendship/profile/" + profileId(1)).then().statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().containsExactly(profileId(3));
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().containsExactly(profileId(1));
        
        //THEN P1 cancels friendship request to P3
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/cancelfriendship/profile/" + profileId(3)).then().statusCode(HttpStatus.SC_OK);
        p1 = profileService.readOne(profileId(1));
        assertThat(p1.getFriends()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p1.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p2 = profileService.readOne(profileId(2));
        assertThat(p2.getFriends()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p2.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        p3 = profileService.readOne(profileId(3));
        assertThat(p3.getFriends()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsTo()).isNotNull().isEmpty();
        assertThat(p3.getFriendshipRequestsFrom()).isNotNull().isEmpty();
        
        // check friendshipEvents history
        checkFriendshipEvents(p1,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_CANCELED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_CANCELED_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_CANCELED_FRIEND_REQUEST);
        checkFriendshipEvents(p2,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_CANCELED_FRIEND_REQUEST);
        checkFriendshipEvents(p3,
                FriendshipEvent.Code.TARGET_SENT_FRIEND_REQUEST,
                FriendshipEvent.Code.YOU_CANCELED_FRIEND_REQUEST,
                FriendshipEvent.Code.TARGET_CANCELED_FRIEND_REQUEST);
    }
    
    /** Ensure a profile can't send a friendship request to himself, to an unknown profile, and
     * can't accept or reject a request that doesn't exist (to prevent unwanted friendships creation
     * or deletion). */
    @Test
    public void shouldNotTransformWrongFriendshipRequest() throws Exception {
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_OK);
        whenP1().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(1))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipRequestNotFoundException"),
                        FIELD_MESSAGE, Matchers.hasSize(2),
                        FIELD_MESSAGE, Matchers.contains(profileId(1), profileId(1)));
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(2))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipRequestNotFoundException"),
                        FIELD_MESSAGE, Matchers.hasSize(2),
                        FIELD_MESSAGE, Matchers.contains(profileId(2), profileId(2)));
        whenP2().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/" + profileId(3))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(FIELD_ERRORS, Matchers.equalTo("FriendshipRequestNotFoundException"),
                        FIELD_MESSAGE, Matchers.hasSize(2),
                        FIELD_MESSAGE, Matchers.contains(profileId(3), profileId(2)));
    }
    
    /** Friendship requests, accept and reject operations need authentication. */
    @Test
    public void shouldNotMakeFriendshipOperations_anonymous() throws Exception {
        whenAnonymous().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/askfriendship/profile/1").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
        whenAnonymous().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/acceptfriendship/profile/1").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
        whenAnonymous().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/rejectfriendship/profile/1").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
        whenAnonymous().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/cancelfriendship/profile/1").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
        whenAnonymous().getRequestSpecification()
                .post(getApiV1() + TEST_API_PROFILE + "/revokefriendship/profile/1").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
    
    private void checkFriendshipEvents(Profile p, FriendshipEvent.Code... codes) {
        Assertions.assertThat(p.getFriendshipEvents()).isNotNull().hasSize(codes.length);
        for (int i = 0; i < codes.length; i++) {
            assertEquals(p.getFriendshipEvents().get(i).getEvent(), codes[i], "friendshipEvents[" + i + "]");
        }
    }
}
