package manon.app.sys.service;

import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class InfoServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private InfoService infoService;
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldGetAppVersion() {
        assertThat(infoService.getAppVersion()).matches(Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+[\\-A-Z]*$"));
    }
}
