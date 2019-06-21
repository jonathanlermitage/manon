package manon.util.web;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.Value;
import manon.service.app.JwtTokenService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.currentTimeMillis;

/** Ready-to-use {@link RequestSpecification} (use {@link #getSpec()}) configured to use a user's credentials. */
@Value
public class Rs {
    
    @Contract(pure = true)
    public Rs(String username, String password, boolean authenticated, String token) {
        if (tokenProvider == null) {
            throw new RuntimeException("mandatory Rs.tokenProvider is not set");
        }
        this.username = username;
        this.password = password;
        this.authenticated = authenticated;
        this.token = token;
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
    
    /** Ready-to-use {@link RequestSpecification} configured to use a user's credentials. */
    public RequestSpecification getSpec() {
        if (authenticated) {
            return RestAssured.given()
                .header("X-Request-Id", "user-" + currentTimeMillis())
                .header("Authorization", "Bearer " + (token == null ? tokenProvider.generateToken(username) : token));
        }
        return RestAssured.given()
            .header("X-Request-Id", "anon-" + currentTimeMillis())
            .auth().none();
    }
    
    /** Get a spec with authentication using a token based on given username and password. */
    @NotNull
    @Contract("_, _ -> new")
    public static Rs authenticated(String username, String password) {
        return new Rs(username, password, true, null);
    }
    
    /** Get a spec with authentication using given pre-built token. */
    @NotNull
    @Contract("_ -> new")
    public static Rs usingToken(String token) {
        return new Rs("", "", true, token);
    }
    
    /** Get a spec with no authentication. */
    @NotNull
    @Contract(" -> new")
    public static Rs anonymous() {
        return new Rs("", "", false, null);
    }
}
