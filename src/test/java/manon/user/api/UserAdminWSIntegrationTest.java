package manon.user.api;

import io.restassured.response.Response;
import manon.user.document.User;
import manon.user.err.UserNotFoundException;
import manon.util.basetest.AbstractInitBeforeClass;
import manon.util.web.UserPage;
import org.junit.jupiter.api.Test;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserAdminWSIntegrationTest extends AbstractInitBeforeClass {
    
    @Override
    public int getNumberOfUsers() {
        return 4;
    }
    
    @Test
    public void shouldFindAllDesc() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?offset=0&size=100&sort=creationDate,DESC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertThat(top >= bottom).as("order").isTrue();
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
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            long top = users.get(i - 1).getCreationDate().getTime();
            long bottom = users.get(i).getCreationDate().getTime();
            assertThat(top <= bottom).as("order").isTrue();
        }
    }
    
    @Test
    public void shouldFindAllSmallPageStartPart() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?size=" + (userCount - 1));
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount - 1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
    }
    
    @Test
    public void shouldFindAllPageEndPart() {
        Response res = whenAdmin().getRequestSpecification()
            .get(API_USER_ADMIN + "/all?page=1&size=" + (userCount - 1));
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        UserPage result = readValue(res, UserPage.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
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
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
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
