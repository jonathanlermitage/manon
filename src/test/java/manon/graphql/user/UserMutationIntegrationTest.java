package manon.graphql.user;

import io.restassured.response.ValidatableResponse;
import manon.document.user.User;
import manon.err.user.UserNotFoundException;
import manon.util.basetest.AbstractIntegrationTest;
import manon.util.web.Rs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class UserMutationIntegrationTest extends AbstractIntegrationTest {
    
    @Test
    public void shouldSetMyNickname() throws UserNotFoundException {
        whenAdmin().getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                "mutation { setMyNickname (nickname: \"foo bar\") { id, username, nickname, version } }"))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK)
            .body("data.setMyNickname.nickname", equalTo("foo bar"));
        
        User userInDb = userService.readByUsername(cfg.getDefaultUserAdminUsername());
        assertThat(userInDb.getNickname()).isEqualTo("foo bar");
    }
    
    @Test
    public void shouldFailSetMyInvalidNickname() throws UserNotFoundException {
        User userInDbBefore = userService.readByUsername(cfg.getDefaultUserAdminUsername());
        
        whenAdmin().getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                "mutation { setMyNickname (nickname: \"foo ?bar\") { id, username, nickname, version } }"))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK)
            .body("errors", hasSize(1),
                "errors[0].message", equalTo("model validation failed: NICKNAME_PATTERN"));
        
        User userInDbAfter = userService.readByUsername(cfg.getDefaultUserAdminUsername());
        assertThat(userInDbAfter).isEqualTo(userInDbBefore);
    }
    
    public Object[][] dataProviderAllowAuthenticated() {
        return new Object[][]{
            {whenActuator(), true},
            {whenAdmin(), true},
            {whenDev(), true},
            {whenP1(), true},
            {whenAnonymous(), false}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderAllowAuthenticated")
    public void shouldAccessSetMyNickname(Rs rs, boolean allowed) {
        ValidatableResponse query = rs.getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                "mutation { setMyNickname (nickname: \"foo bar\") { id, username, nickname, version } }"))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK);
        if (allowed) {
            query.body("errors", equalTo(null),
                "data.setMyNickname.nickname", equalTo("foo bar"));
        } else {
            query.body("errors", hasSize(1),
                "errors[0].message", equalTo("Access is denied"),
                "errors[0].path[0]", equalTo("setMyNickname"));
        }
    }
}
