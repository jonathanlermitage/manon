package manon.app.info.api;

import io.restassured.response.Response;
import manon.util.basetest.InitBeforeClass;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertTrue;

public class InfoWSTest extends InitBeforeClass {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldGetAppVersion() {
        Response res = whenAdmin().getRequestSpecification().get(API_SYS + "/info/app-version");
        res.then().statusCode(SC_OK);
        assertTrue(Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+[\\-A-Z]*$").matcher(res.asString()).matches());
    }
}
