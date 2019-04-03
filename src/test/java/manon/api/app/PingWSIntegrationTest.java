package manon.api.app;

import io.restassured.RestAssured;
import manon.err.app.PingException;
import manon.service.app.PingService;
import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class PingWSIntegrationTest extends AbstractIntegrationTest {
    
    @SpyBean
    protected PingService pingService;
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @BeforeEach
    public void setUpMocks() {
        Mockito.clearInvocations(pingService);
    }
    
    @Test
    public void shouldPingAccessibleUrl() throws PingException, UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + RestAssured.port + "/actuator/health";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getRequestSpecification()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_OK);
        verify(pingService, Mockito.times(1)).ping(any());
    }
    
    @Test
    public void shouldNotPingInaccessibleUrl() throws PingException, UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + RestAssured.port + "/actuator/health2";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getRequestSpecification()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_BAD_REQUEST);
        verify(pingService, Mockito.times(2)).ping(any());
    }
    
    @Test
    public void shouldNotPingInvalidUrl() throws PingException, UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + 1 + "/actuator/health2";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getRequestSpecification()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_BAD_REQUEST);
        verify(pingService, Mockito.times(2)).ping(any());
    }
}
