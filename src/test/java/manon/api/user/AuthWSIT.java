package manon.api.user;

import io.restassured.response.Response;
import manon.document.user.UserEntity;
import manon.model.user.form.UserLogin;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;

import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthWSIT extends AbstractIT {

    @Test
    public void shouldLogin() {
        Response res = whenAnonymous().getSpec()
            .body(UserLogin.builder().username(name(1)).password(pwd(1)).build())
            .contentType(JSON)
            .post(API_USER + "/auth/authorize");
        res.then()
            .statusCode(SC_OK);
        assertThat(res.asString()).isNotEmpty();
    }

    @Test
    public void shouldUseToken() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        Response res = whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .get(API_USER);
        res.then()
            .statusCode(SC_OK);
        UserEntity webUser = readValue(res, UserEntity.class);
        UserEntity dbUser = userService.readOne(userId(1)).toBuilder().password(null).build();
        assertThat(webUser).isEqualTo(dbUser);
        assertThat(webUser.getUserSnapshots()).isNull();
    }

    @Test
    public void shouldReuseToken() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        UserEntity dbUser = userService.readOne(userId(1)).toBuilder().password(null).build();
        for (int i = 0; i < 3; i++) {
            Response res = whenAnonymous().getSpec()
                .header("Authorization", "Bearer " + jwt)
                .get(API_USER);
            res.then()
                .statusCode(SC_OK);
            UserEntity webUser = readValue(res, UserEntity.class);
            assertThat(webUser).isEqualTo(dbUser);
            assertThat(webUser.getUserSnapshots()).isNull();
        }
    }

    @Test
    public void shouldRenewToken() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        Response res = whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .post(API_USER + "/auth/renew");
        res.then()
            .statusCode(SC_OK);
        String newJwt = res.asString();
        assertThat(newJwt).isNotEmpty().isNotEqualTo(jwt);

        Response newRes = whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .get(API_USER);
        newRes.then()
            .statusCode(SC_OK);
        UserEntity webUser = readValue(newRes, UserEntity.class);
        UserEntity dbUser = userService.readOne(userId(1)).toBuilder().password(null).build();
        assertThat(webUser).isEqualTo(dbUser);
        assertThat(webUser.getUserSnapshots()).isNull();
    }

    @Test
    public void shouldRejectTokenIfAccountBanned() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        registrationService.ban(userId(1));

        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .get(API_USER)
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    public void shouldRejectTokenIfAccountDeleted() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        registrationService.delete(userId(1));

        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .get(API_USER)
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    public void shouldRejectTokenIfAccountSuspended() {
        String jwt = loginAndReturnToken(name(1), pwd(1));

        registrationService.suspend(userId(1));

        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt)
            .get(API_USER)
            .then()
            .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    public void shouldLogOutAllForCurrentUser() {
        //GIVEN user 1 gets 2 tokens, user 2 get 1 token
        String jwt1a = loginAndReturnToken(name(1), pwd(1));
        String jwt1b = loginAndReturnToken(name(1), pwd(1));
        String jwt2 = loginAndReturnToken(name(2), pwd(2));

        //WHEN user 1 invalidates all his tokens
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt1a)
            .post(API_USER + "/auth/logout/all")
            .then()
            .statusCode(SC_OK);

        //THEN user 1 can't user his first token
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt1a)
            .get(API_USER)
            .then()
            .statusCode(SC_UNAUTHORIZED);

        //THEN user 1 can't user his second token
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt1b)
            .get(API_USER)
            .then()
            .statusCode(SC_UNAUTHORIZED);

        //THEN user 2 is not affected by token invalidation asked by user 1
        whenAnonymous().getSpec()
            .header("Authorization", "Bearer " + jwt2)
            .get(API_USER)
            .then()
            .statusCode(SC_OK);

        //THEN user 1 can authenticate and use new token
        whenAuthenticated(name(1), pwd(1)).getSpec()
            .header("Authorization", "Bearer " + jwt1b)
            .get(API_USER)
            .then()
            .statusCode(SC_OK);
    }
}
