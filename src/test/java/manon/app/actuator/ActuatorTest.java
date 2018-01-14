package manon.app.actuator;

import lombok.extern.slf4j.Slf4j;
import manon.util.basetest.InitBeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@Slf4j
public class ActuatorTest extends InitBeforeClass {
    
    /**
     * Spring Boot 1 actuator endpoints.
     * <p>
     * See http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html
     */
    @DataProvider
    public Object[][] dataProviderSpringBoot1Actuator() {
        return new Object[][]{
                {"/beans"},
                {"/configprops"},
                {"/dump"},
                {"/env"},
                {"/health"},
                {"/info"},
                {"/metrics"}
        };
    }
    
    /**
     * Spring Boot 2 actuator endpoints and configured visibility to public.
     * <p>
     * See https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0.0-M7-Release-Notes<br>
     * See https://docs.spring.io/spring-boot/docs/2.0.0.M7/reference/htmlsingle/#production-ready
     */
    @DataProvider
    public Object[][] dataProviderSpringBoot2Actuator() {
        return new Object[][]{
                {"/actuator", false},
                {"/actuator/health", true},
                {"/actuator/info", true},
                {"/actuator/env", false},
                {"/actuator/loggers", false},
                {"/actuator/mappings", false},
                {"/actuator/metrics", false},
                {"/actuator/scheduledtasks", false}
        };
    }
    
    @Test(dataProvider = "dataProviderSpringBoot1Actuator")
    public void shouldNotGetSpringBoot1Actuator(String endpoint) {
        whenAdmin().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(SC_FORBIDDEN);
        whenP1().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(SC_FORBIDDEN);
        whenAnonymous().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = "dataProviderSpringBoot2Actuator")
    public void shouldGetSpringBoot2Actuator(String endpoint, boolean isPublic) {
        whenAdmin().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(SC_OK);
        whenP1().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(isPublic ? SC_OK : SC_FORBIDDEN);
        whenAnonymous().getRequestSpecification()
                .get(endpoint).then()
                .statusCode(isPublic ? SC_OK : SC_UNAUTHORIZED);
    }
}
