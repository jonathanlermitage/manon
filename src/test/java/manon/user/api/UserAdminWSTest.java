package manon.user.api;

import io.restassured.response.Response;
import manon.user.document.User;
import manon.user.err.UserNotFoundException;
import manon.util.basetest.AbstractInitBeforeClass;
import manon.util.web.UserPage;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static manon.app.config.AbstractControllerAdvice.FIELD_ERRORS;
import static manon.user.model.RegistrationState.ACTIVE;
import static manon.user.model.RegistrationState.BANNED;
import static manon.user.model.RegistrationState.SUSPENDED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserAdminWSTest extends AbstractInitBeforeClass {
    
    @Test
    public void shouldFindAllDesc() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?offset=0&size=100&sort=creationDate,DESC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), userCount);
        assertEquals(result.getTotalElements(), userCount);
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertTrue(top >= bottom, "order");
        }
    }
    
    @Test
    public void shouldFindAllAsc() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?offset=0&size=100&sort=creationDate,ASC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), userCount);
        assertEquals(result.getTotalElements(), userCount);
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertTrue(top <= bottom, "order");
        }
    }
    
    @Test
    public void shouldFindAllSmallPageStartPart() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?size=3");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), 3);
        assertEquals(result.getTotalElements(), userCount);
    }
    
    @Test
    public void shouldFindAllSmallPageEndPart() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?page=1&size=3");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), userCount - 3);
        assertEquals(result.getTotalElements(), userCount);
    }
    
    @Test
    public void shouldFindAllSmallPageMiddlePart() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?size=1");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertEquals(users.size(), 1);
        assertEquals(result.getTotalElements(), userCount);
    }
    
    @Test
    public void shouldCycleRegistrationState() {
        List<String> uids = Arrays.asList(userId(1), userId(2));
        for (String uid : uids) {
            whenAdmin().getRequestSpecification()
                .post(API_USER_ADMIN + "/" + uid + "/suspend")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(SUSPENDED.name()));
            whenAdmin().getRequestSpecification()
                .post(API_USER_ADMIN + "/" + uid + "/ban")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(BANNED.name()));
            whenAdmin().getRequestSpecification()
                .post(API_USER_ADMIN + "/" + uid + "/activate")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(ACTIVE.name()));
        }
    }
    
    @Test
    public void shouldNotActivateUnknown() {
        whenAdmin().getRequestSpecification()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/activate")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(FIELD_ERRORS, equalTo(UserNotFoundException.class.getSimpleName()));
    }
    
    @Test
    public void shouldNotBanUnknown() {
        whenAdmin().getRequestSpecification()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/ban")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(FIELD_ERRORS, equalTo(UserNotFoundException.class.getSimpleName()));
    }
    
    @Test
    public void shouldNotSuspendUnknown() {
        whenAdmin().getRequestSpecification()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/suspend")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(FIELD_ERRORS, equalTo(UserNotFoundException.class.getSimpleName()));
    }
}
