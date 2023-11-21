package manon.api.app;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Slf4j
class ActuatorCtrlIT extends AbstractMockIT {

    /**
     * Spring Boot 2 actuator endpoints and configured visibility to public.
     * <p>
     * See <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html">Spring Reference</a> and
     * <a href="https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/">Actuator API</a>.
     * @return HTTP verb; is public; is enabled.
     */
    Object[][] dataProviderShouldGetSpringBoot2Actuator() {
        return new Object[][]{
            {GET, "/actuator", false, true},
            {GET, "/actuator/auditevents", false, false},
            {GET, "/actuator/beans", false, false},
            {GET, "/actuator/conditions", false, false},
            {GET, "/actuator/configprops", false, true},
            {GET, "/actuator/env", false, true},
            {GET, "/actuator/health", true, true},
            {GET, "/actuator/heapdump", false, false},
            {GET, "/actuator/httptrace", false, false},
            {GET, "/actuator/info", false, true},
            {GET, "/actuator/loggers", false, false},
            {GET, "/actuator/metrics", false, true},
            {GET, "/actuator/prometheus", true, true},
            {GET, "/actuator/scheduledtasks", false, true},
            {POST, "/actuator/shutdown", false, false},
            {GET, "/actuator/threaddump", false, false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldGetSpringBoot2Actuator")
    void shouldGetSpringBoot2Actuator(HttpMethod verb, String endpoint, boolean isPublic, boolean isEnabled) {
        call(whenActuator(), verb, endpoint).statusCode(isEnabled ? SC_OK : SC_NOT_FOUND);
        call(whenAdmin(), verb, endpoint).statusCode(isEnabled ? SC_OK : SC_NOT_FOUND);
        call(whenP1(), verb, endpoint).statusCode(isPublic ? isEnabled ? SC_OK : SC_NOT_FOUND : SC_FORBIDDEN);
        call(whenAnonymous(), verb, endpoint).statusCode(isPublic ? isEnabled ? SC_OK : SC_NOT_FOUND : SC_UNAUTHORIZED);
    }

    private ValidatableResponse call(Rs rs, HttpMethod verb, String endpoint) {
        RequestSpecification spec = rs.getSpec();
        if (verb.equals(GET)) {
            return spec.get(endpoint).then();
        }
        if (verb.equals(POST)) {
            return spec.post(endpoint).then();
        }
        throw new IllegalArgumentException("invalid verb:" + verb);
    }
}
