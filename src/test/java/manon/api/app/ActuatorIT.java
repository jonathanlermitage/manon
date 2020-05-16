package manon.api.app;

import io.restassured.response.ValidatableResponse;
import manon.model.user.UserRole;
import manon.util.basetest.AbstractIT;
import manon.util.web.Rs;
import net.javacrumbs.jsonunit.JsonMatchers;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.util.ResourceUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.Matchers.startsWithIgnoringCase;

public class ActuatorIT extends AbstractIT {

    @Override
    public int getNumberOfUsers() {
        return 1;
    }

    /**
     * Health actuator endpoint should show minimal information to anonymous and users without the {@link UserRole#ACTUATOR} role.
     * @return user; true if full information should be shown, false for minimal information.
     */
    public Object[][] dataProviderShouldGetFullHealth() {
        return new Object[][]{
            {whenActuator(), true},
            {whenAdmin(), true},
            {whenP1(), false},
            {whenAnonymous(), false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldGetFullHealth")
    public void shouldGetHealthActuatorWithMinimalOrFullInformation(Rs rs, boolean isFullInfo) {
        ValidatableResponse response = rs.getSpec().get("/actuator/health").then();
        if (isFullInfo) {
            response.body(JsonMatchers.jsonEquals(ResourceUtils.resource("expected/actuator-health-full.json"))
                .when(Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_FIELDS));
        } else {
            response.body(JsonMatchers.jsonEquals(ResourceUtils.resource("expected/actuator-health.json")));
        }
    }

    @Test
    public void shouldGetInfoActuatorWhenAdmin() {
        whenAdmin().getSpec().get("/actuator/info").then().body(
            startsWithIgnoringCase("{\"app\":{\"version\":\"" + cfg.getVersion() + "\",\"name\":\"manon\"")
        );
    }
}
