package manon.app.info.api;

import io.restassured.response.Response;
import manon.util.basetest.AbstractInitBeforeClass;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class InfoWSTest extends AbstractInitBeforeClass {
    
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
}
