package manon.util.web;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import manon.model.user.form.UserLogin;
import manon.service.app.JwtTokenService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.restassured.http.ContentType.JSON;
import static manon.app.Globals.API.API_USER;
import static org.apache.http.HttpStatus.SC_OK;

/** Ready-to-use {@link RequestSpecification} (use {@link #getSpec()}) configured to use a user's credentials. */
@Getter
@EqualsAndHashCode
@ToString
public class Rs {

    @Contract(pure = true)
    public Rs(String username, String password, boolean authenticated, String token, AuthMode authMode) {
        if (tokenProvider == null) {
            throw new RuntimeException("mandatory Rs.tokenProvider is not set");
        }
        this.username = username;
        this.password = password;
        this.authenticated = authenticated;
        this.token = token;
        this.authMode = authMode;
    }

    /** Mandatory: the service that's used to create authentication tokens. */
    public static JwtTokenService tokenProvider;

    /** User's username. Blank if anonymous. */
    private String username;
    /** User's raw password. Blank if anonymous. */
    private String password;
    /** Indicates if an authentication is performed. */
    private boolean authenticated;
    /** Authentication token. Will be computed in {@link #getSpec()}} if null, otherwise use provided value. */
    private String token;
    /** How to retrieve authentication token. */
    private AuthMode authMode;

    /** Ready-to-use {@link RequestSpecification} configured to use a user's credentials. */
    public RequestSpecification getSpec() {
        if (authenticated) {
            if (token == null) {
                switch (authMode) {
                    case FORCED_VIA_SERVICE:
                        token = tokenProvider.generateToken(username);
                        break;
                    case REGULAR_VIA_API:
                        token = loginAndReturnToken(username, password);
                        break;
                    default:
                        throw new RuntimeException("authentication wanted but mandatory Rs.authMode is not set");
                }
            }
            return RestAssured.given()
                .header("Authorization", "Bearer " + token);
        }
        return RestAssured.given()
            .auth().none();
    }

    public static Response login(String username, String password) {
        return anonymous().getSpec()
            .body(UserLogin.builder().username(username).password(password).build())
            .contentType(JSON)
            .post(API_USER + "/auth/authorize");
    }

    public static String loginAndReturnToken(String username, String password) {
        Response res = login(username, password);
        res.then()
            .statusCode(SC_OK);
        return res.asString();
    }

    /** Get a spec with authentication using a token based on given username and password. */
    @NotNull
    @Contract("_, _, _ -> new")
    public static Rs authenticated(String username, String password, AuthMode authMode) {
        return new Rs(username, password, true, null, authMode);
    }

    /** Get a spec with authentication using given pre-built token. */
    @NotNull
    @Contract("_, _ -> new")
    public static Rs usingToken(String token, AuthMode authMode) {
        return new Rs("", "", true, token, authMode);
    }

    /** Get a spec with no authentication. */
    @NotNull
    @Contract(" -> new")
    public static Rs anonymous() {
        return new Rs("", "", false, null, null);
    }
}
