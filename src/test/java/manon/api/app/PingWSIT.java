package manon.api.app;

import io.restassured.RestAssured;
import manon.util.basetest.AbstractNoUserIT;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class PingWSIT extends AbstractNoUserIT {

    @Test
    public void shouldPingAccessibleUrl() throws UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + RestAssured.port + "/actuator/health";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getSpec()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_OK);
        verify(pingService, Mockito.times(1)).ping(any());
    }

    @Test
    public void shouldNotPingInaccessibleUrl() throws UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + RestAssured.port + "/actuator/health2";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getSpec()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_BAD_REQUEST);
        verify(pingService, Mockito.times(2)).ping(any());
    }

    @Test
    public void shouldNotPingInvalidUrl() throws UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + 1 + "/actuator/health2";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        whenAdmin().getSpec()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(SC_BAD_REQUEST);
        verify(pingService, Mockito.times(2)).ping(any());
    }
}
