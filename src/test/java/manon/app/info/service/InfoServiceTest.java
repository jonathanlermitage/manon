package manon.app.info.service;

import manon.util.basetest.InitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

public class InfoServiceTest extends InitBeforeClass {
    
    @Autowired
    private InfoService infoService;
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldGetAppVersion() {
        Assert.assertTrue(Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+[\\-A-Z]*$").matcher(infoService.getAppVersion()).matches());
    }
}
