package manon.util.web;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.currentTimeMillis;

/** Ready-to-use {@link RequestSpecification} (use {@link #getSpec()}) configured to use a user's credentials. */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Rs {
    
    /** Ready-to-use {@link RequestSpecification} configured to use a user's credentials. */
    private RequestSpecification spec;
    /** User's username. Blank if anonymous. */
    private String username;
    /** User's raw password. Blank if anonymous. */
    private String password;
    
    @NotNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return username + ":" + password;
    }
    
    /** Get a spec using preemptive authentication view. Use it for integration tests. */
    @NotNull
    @Contract("_, _ -> new")
    public static Rs authenticatedPreemptively(String username, String password) {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "user-" + currentTimeMillis())
            .auth().preemptive().basic(username, password), username, password);
    }
    
    /** Get a spec with authentication. Prefer {@link #authenticatedPreemptively(String, String)} for integration tests. */
    @NotNull
    @Contract("_, _ -> new")
    public static Rs authenticated(String username, String password) {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "user-" + currentTimeMillis())
            .auth().basic(username, password), username, password);
    }
    
    /** Get a spec with no authentication. */
    @NotNull
    @Contract(" -> new")
    public static Rs anonymous() {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "anon-" + currentTimeMillis())
            .auth().none(), "", "");
    }
}
