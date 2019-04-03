package manon.api.app;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import manon.util.basetest.AbstractMockTest;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PingCtrlTest extends AbstractMockTest {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldGetSwaggerIndex(Rs rs, Integer status) throws UnsupportedEncodingException {
        String wsUrl = RestAssured.baseURI + ":" + RestAssured.port + "/actuator/health";
        String urlencodedWsUrl = URLEncoder.encode(wsUrl, StandardCharsets.UTF_8.name());
        rs.getRequestSpecification()
            .get(API_SYS + "/ping/" + urlencodedWsUrl)
            .then()
            .statusCode(status);
    }
}
