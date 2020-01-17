package manon.api.user;

import io.restassured.response.Response;
import manon.document.user.User;
import manon.err.user.UserNotFoundException;
import manon.util.basetest.AbstractIT;
import manon.util.web.Page;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static manon.model.user.RegistrationState.ACTIVE;
import static manon.model.user.RegistrationState.BANNED;
import static manon.model.user.RegistrationState.SUSPENDED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserAdminWSIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 4;
    }

    @Test
    public void shouldFindAllDesc() {
        Response res = whenAdmin().getSpec()
            .get(API_USER_ADMIN + "/all?offset=0&size=100&sort=creationDate,DESC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            LocalDateTime top = users.get(i - 1).getCreationDate();
            LocalDateTime bottom = users.get(i).getCreationDate();
            assertThat(top).as("order").isAfterOrEqualTo(bottom);
        }
    }

    @Test
    public void shouldFindAllAsc() {
        Response res = whenAdmin().getSpec()
            .get(API_USER_ADMIN + "/all?offset=0&size=100&sort=creationDate,ASC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            LocalDateTime top = users.get(i - 1).getCreationDate();
            LocalDateTime bottom = users.get(i).getCreationDate();
            assertThat(top).as("order").isBeforeOrEqualTo(bottom);
        }
    }

    @Test
    public void shouldFindAllSmallPageStartPart() {
        Response res = whenAdmin().getSpec()
            .get(API_USER_ADMIN + "/all?size=" + (userCount - 1));
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount - 1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
    }

    @Test
    public void shouldFindAllPageEndPart() {
        Response res = whenAdmin().getSpec()
            .get(API_USER_ADMIN + "/all?page=1&size=" + (userCount - 1));
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
    }

    @Test
    public void shouldFindAllSmallPageMiddlePart() {
        Response res = whenAdmin().getSpec()
            .get(API_USER_ADMIN + "/all?size=1");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
    }

    @Test
    public void shouldCycleRegistrationState() {
        List<Long> uids = Arrays.asList(userId(1), userId(2));
        for (long uid : uids) {
            whenAdmin().getSpec()
                .post(API_USER_ADMIN + "/" + uid + "/suspend")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(SUSPENDED.name()));
            whenAdmin().getSpec()
                .post(API_USER_ADMIN + "/" + uid + "/ban")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(BANNED.name()));
            whenAdmin().getSpec()
                .post(API_USER_ADMIN + "/" + uid + "/activate")
                .then()
                .statusCode(SC_OK)
                .contentType(TEXT)
                .body(equalTo(ACTIVE.name()));
        }
    }

    @Test
    public void shouldNotActivateUnknown() {
        whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/activate")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(MANAGED_ERROR_TYPE, equalTo(UserNotFoundException.class.getSimpleName()));
    }

    @Test
    public void shouldNotBanUnknown() {
        whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/ban")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(MANAGED_ERROR_TYPE, equalTo(UserNotFoundException.class.getSimpleName()));
    }

    @Test
    public void shouldNotSuspendUnknown() {
        whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/" + UNKNOWN_ID + "/suspend")
            .then()
            .statusCode(SC_NOT_FOUND)
            .contentType(JSON)
            .body(MANAGED_ERROR_TYPE, equalTo(UserNotFoundException.class.getSimpleName()));
    }

    @Test
    public void shouldSearchWithPredicateOnRegistrationState() {
        Response res = whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/search?offset=0&size=100&sort=creationDate,DESC&registrationState=ACTIVE");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            LocalDateTime top = users.get(i - 1).getCreationDate();
            LocalDateTime bottom = users.get(i).getCreationDate();
            assertThat(top).as("order").isAfterOrEqualTo(bottom);
        }
    }

    @Test
    public void shouldSearchWithPredicateOnUsername() {
        Response res = whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/search?offset=0&size=100&sort=creationDate,DESC&username=" + name(1));
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(name(1));
    }

    @Test
    public void shouldSearchWithoutPredicate() {
        Response res = whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/search?offset=0&size=100&sort=creationDate,DESC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            LocalDateTime top = users.get(i - 1).getCreationDate();
            LocalDateTime bottom = users.get(i).getCreationDate();
            assertThat(top).as("order").isAfterOrEqualTo(bottom);
        }
    }

    @Test
    public void shouldSearchByIdentityWithPredicate() {
        Response res = whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/search/identity?offset=0&size=100&sort=creationDate,DESC" +
                "&username=" + name(1) + "&nickname=foo1&email=email1");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(users.get(0).getUsername()).isEqualTo(name(1));
    }

    @Test
    public void shouldSearchByIdentityWithoutPredicate() {
        Response res = whenAdmin().getSpec()
            .post(API_USER_ADMIN + "/search/identity?offset=0&size=100&sort=creationDate,DESC");
        res.then()
            .contentType(JSON)
            .statusCode(SC_OK);
        Page<User> result = readPage(res, User.class);
        List<User> users = result.getContent();
        assertThat(users).hasSize(userCount);
        assertThat(result.getTotalElements()).isEqualTo(userCount);
        for (int i = 1; i < users.size(); i++) {
            LocalDateTime top = users.get(i - 1).getCreationDate();
            LocalDateTime bottom = users.get(i).getCreationDate();
            assertThat(top).as("order").isAfterOrEqualTo(bottom);
        }
    }
}
