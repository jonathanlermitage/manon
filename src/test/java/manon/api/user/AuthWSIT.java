package manon.api.user;

import io.restassured.response.Response;
import manon.document.user.User;
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
        User webUser = readValue(res, User.class);
        User dbUser = userService.readOne(userId(1)).toBuilder().password(null).build();
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
}
