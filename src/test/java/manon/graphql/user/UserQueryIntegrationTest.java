package manon.graphql.user;

import io.restassured.response.ValidatableResponse;
import manon.document.user.User;
import manon.document.user.UserSnapshot;
import manon.err.user.UserNotFoundException;
import manon.service.user.UserSnapshotService;
import manon.util.basetest.AbstractIntegrationTest;
import manon.util.web.Rs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class UserQueryIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private UserSnapshotService userSnapshotService;
    
    @Test
    public void shouldGetUserById() throws UserNotFoundException {
        userSnapshotService.save(Arrays.asList(
            UserSnapshot.from(user(1)),
            UserSnapshot.from(user(1))
        ));
        User userToRead = userService.readByUsername(name(1));
        
        whenAdmin().getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                String.format("{ userById (id: %s) { username, userSnapshots { username } } }", userToRead.getId())))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK)
            .body("data.userById.username", equalTo(userToRead.getUsername()),
                "data.userById.userSnapshots", hasSize(2),
                "data.userById.userSnapshots[0].username", equalTo(userToRead.getUsername()),
                "data.userById.userSnapshots[1].username", equalTo(userToRead.getUsername()));
    }
    
    @Test
    public void shouldNotGetUserByUnknownId() {
        whenAdmin().getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                String.format("{ userById (id: %s) { username, userSnapshots { username } } }", UNKNOWN_ID)))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK)
            .body("data", equalTo(null));
    }
    
    @Test
    public void shouldGetMyUser() {
        userSnapshotService.save(Arrays.asList(
            UserSnapshot.from(user(1)),
            UserSnapshot.from(user(1))
        ));
        
        whenP1().getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                "{ myUser { username, userSnapshots { username } } }"))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK)
            .body("data.myUser.username", equalTo(name(1)),
                "data.myUser.userSnapshots", hasSize(2),
                "data.myUser.userSnapshots[0].username", equalTo(name(1)),
                "data.myUser.userSnapshots[1].username", equalTo(name(1)));
    }
    
    public Object[][] dataProviderAllowDev() {
        return new Object[][]{
            {whenActuator(), false},
            {whenAdmin(), true},
            {whenDev(), true},
            {whenP1(), false},
            {whenAnonymous(), false}
        };
    }
    
    @ParameterizedTest
    @MethodSource("dataProviderAllowDev")
    public void shouldAccessGetUserById(Rs rs, boolean allowed) throws UserNotFoundException {
        User userToRead = userService.readByUsername(name(1));
        
        ValidatableResponse query = rs.getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                String.format("{ userById (id: %s) { username, userSnapshots { username} } }", userToRead.getId())))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK);
        if (allowed) {
            query.body("errors", equalTo(null),
                "data.userById.username", equalTo(userToRead.getUsername()));
        } else {
            query.body("errors", hasSize(1),
                "errors[0].message", equalTo("Access is denied"),
                "errors[0].path[0]", equalTo("userById"));
        }
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
    public void shouldAccessGetMyUser(Rs rs, boolean allowed) {
        ValidatableResponse query = rs.getRequestSpecification()
            .body(Collections.singletonMap(
                "query",
                "{ myUser { username, userSnapshots { username } } }"))
            .post(API_GRAPHQL)
            .then()
            .statusCode(SC_OK);
        if (allowed) {
            query.body("errors", equalTo(null),
                "data.myUser.username", equalTo(rs.getUsername()));
        } else {
            query.body("errors", hasSize(1),
                "errors[0].message", equalTo("Access is denied"),
                "errors[0].path[0]", equalTo("myUser"));
        }
    }
}
