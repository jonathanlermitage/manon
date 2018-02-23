package manon.util;

import manon.matchmaking.LobbyLeague;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.testng.Assert.assertEquals;

public class ToolsTest {
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyIsBlank() {
        return new Object[][]{
                {null, true},
                {"", true},
                {"   ", true},
                {"a", false}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyIsBlank")
    public void shouldVerifyIsBlank(String input, Object expected) {
        assertEquals(Tools.isBlank(input), expected);
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyShortenLog() {
        return new Object[][]{
                {"abc", "abc"},
                {repeat("a", 200), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa... (long string, length=200)"},
                {repeat("ab", 100), "ababababababababababababababab... (long string, length=200)"},
                {repeat("A", 250), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA... (long string, length=250)"},
                {null, "null"},
                {1, "1"},
                {1000, "1000"},
                {1000.1, "1000.1"},
                {LobbyLeague.COMPETITIVE, "COMPETITIVE"},
                {LobbyLeague.REGULAR, "REGULAR"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyShortenLog")
    public void shouldVerifyShortenLog(Object input, Object expected) {
        assertEquals(Tools.shortenLog(input), expected);
    }
    
    @DataProvider
    public Object[][] dataProviderShouldVerifyShortenAndAnonymizeLog() {
        return new Object[][]{
                {"abc", "***"},
                {repeat("a", 200), "******************************... (long string, length=200)"},
                {repeat("ab", 100), "******************************... (long string, length=200)"},
                {repeat("A", 250), "******************************... (long string, length=250)"},
                {null, "null"},
                {1, "*"},
                {1000, "****"},
                {1000.1, "******"},
                {LobbyLeague.COMPETITIVE, "***********"},
                {LobbyLeague.REGULAR, "*******"}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldVerifyShortenAndAnonymizeLog")
    public void shouldVerifyShortenAndAnonymizeLog(Object input, Object expected) {
        assertEquals(Tools.shortenAndAnonymizeLog(input), expected);
    }
}
