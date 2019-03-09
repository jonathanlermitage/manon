package manon.api.app;

import io.restassured.response.Response;
import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class InfoWSIntegrationTest extends AbstractIntegrationTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldGetAppVersion() {
        Response res = whenAdmin().getRequestSpecification().get(API_SYS + "/info/app-version");
        res.then().statusCode(SC_OK);
        assertThat(res.asString()).matches(Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+[\\-A-Z]*$"));
    }
    
    @Test
    public void shouldGetUp() {
        Response res = whenAnonymous().getRequestSpecification().get(API_SYS + "/info/up");
        res.then().statusCode(SC_OK);
        assertThat(res.asString()).isEqualTo("UP");
    }
}
