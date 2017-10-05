package manon.user.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.user.document.User;
import manon.util.basetest.InitBeforeClass;
import manon.util.basetest.Rs;
import manon.util.web.UserPage;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static manon.app.config.ControllerAdvice.FIELD_ERRORS;
import static manon.app.config.ControllerAdvice.FIELD_MESSAGE;
import static manon.user.registration.RegistrationStateEnum.ACTIVE;
import static manon.user.registration.RegistrationStateEnum.BANNED;
import static manon.user.registration.RegistrationStateEnum.SUSPENDED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserWSTest extends InitBeforeClass {
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldFindAllDesc(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER + "/all?offset=0&size=100&sort=creationDate,DESC");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(NUMBER_OF_USERS, users.size());
        assertEquals(NUMBER_OF_USERS, result.getTotalElements());
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertTrue(top >= bottom, "order");
        }
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldFindAllAsc(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER + "/all?offset=0&size=100&sort=creationDate,ASC");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(NUMBER_OF_USERS, users.size());
        assertEquals(NUMBER_OF_USERS, result.getTotalElements());
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertTrue(top <= bottom, "order");
        }
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldFindAllSmallPageStartPart(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER + "/all?size=3");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), 3);
        assertEquals(result.getTotalElements(), NUMBER_OF_USERS);
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldFindAllSmallPageEndPart(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER + "/all?page=1&size=3");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), NUMBER_OF_USERS - 3);
        assertEquals(result.getTotalElements(), NUMBER_OF_USERS);
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldFindAllSmallPageMiddlePart(Rs rs) throws IOException {
        Response res = rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER + "/all?size=1");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), 1);
        assertEquals(result.getTotalElements(), NUMBER_OF_USERS);
    }
    
    @Test
    public void shouldNotFindAll_anonymous() {
        whenAnonymous().getRequestSpecification()
                .get(getApiV1() + TEST_API_USER)
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS_NO_ADMIN)
    public void shouldNotFindAll_roleNonAdmin(Rs rs) {
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_USER)
                .then()
                .statusCode(SC_FORBIDDEN);
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldCycleRegistrationState(Rs rs) {
        List<String> uids = Arrays.asList(userid(1), userid(2));
        for (String uid : uids) {
            rs.getRequestSpecification()
                    .post(getApiV1() + TEST_API_USER + "/" + uid + "/suspend")
                    .then()
                    .statusCode(SC_OK)
                    .contentType(ContentType.TEXT)
                    .body(equalTo(SUSPENDED.name()));
            rs.getRequestSpecification()
                    .post(getApiV1() + TEST_API_USER + "/" + uid + "/ban")
                    .then()
                    .statusCode(SC_OK)
                    .contentType(ContentType.TEXT)
                    .body(equalTo(BANNED.name()));
            rs.getRequestSpecification()
                    .post(getApiV1() + TEST_API_USER + "/" + uid + "/activate")
                    .then()
                    .statusCode(SC_OK)
                    .contentType(ContentType.TEXT)
                    .body(equalTo(ACTIVE.name()));
        }
    }
    
    @Test
    public void shouldNotCycleRegistrationState_anonymous() {
        String uid = userid(1);
        for (String verb : Arrays.asList("activate", "ban", "suspend")) {
            whenAnonymous().getRequestSpecification()
                    .post(getApiV1() + TEST_API_USER + "/" + uid + "/" + verb)
                    .then()
                    .statusCode(SC_UNAUTHORIZED);
        }
    }
    
    @Test(dataProvider = DP_RS_USERS_NO_ADMIN)
    public void shouldNotCycleRegistrationState_roleNonAdmin(Rs rs) {
        String uid = userid(1);
        for (String verb : Arrays.asList("activate", "ban", "suspend")) {
            rs.getRequestSpecification()
                    .post(getApiV1() + TEST_API_USER + "/" + uid + "/" + verb)
                    .then()
                    .statusCode(SC_FORBIDDEN);
        }
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldNotActivateUnknown(Rs rs) {
        rs.getRequestSpecification()
                .post(getApiV1() + TEST_API_USER + "/" + UNKNOWN_USER_ID + "/activate")
                .then()
                .statusCode(SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body(FIELD_ERRORS, equalTo("UserNotFoundException"))
                .body(FIELD_MESSAGE, equalTo(UNKNOWN_USER_ID));
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldNotBanUnknown(Rs rs) {
        rs.getRequestSpecification()
                .post(getApiV1() + TEST_API_USER + "/" + UNKNOWN_USER_ID + "/ban")
                .then()
                .statusCode(SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body(FIELD_ERRORS, equalTo("UserNotFoundException"))
                .body(FIELD_MESSAGE, equalTo(UNKNOWN_USER_ID));
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldNotSuspendUnknown(Rs rs) {
        rs.getRequestSpecification()
                .post(getApiV1() + TEST_API_USER + "/" + UNKNOWN_USER_ID + "/suspend")
                .then()
                .statusCode(SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body(FIELD_ERRORS, equalTo("UserNotFoundException"))
                .body(FIELD_MESSAGE, equalTo(UNKNOWN_USER_ID));
    }
}
