package manon.util;

import manon.matchmaking.LobbyLeagueEnum;
import manon.user.form.UserFieldEnum;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.testng.Assert.assertEquals;

public class ToolsTest {
    
    @DataProvider(name = "dataProviderIsBlank")
    public Object[][] dataProviderIsBlank() {
        return new Object[][]{
                {null, true},
                {"", true},
                {"   ", true},
                {"a", false}
        };
    }
    
    @Test(dataProvider = "dataProviderIsBlank")
    public void shouldVerifyIsBlank(String input, Object expected) {
        assertEquals(Tools.isBlank(input), expected);
    }
    
    @DataProvider(name = "dataProviderShortenLog")
    public Object[][] dataProviderShortenLog() {
        return new Object[][]{
                {"abc", "abc"},
                {repeat("a", 200), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa... (long string, length=200)"},
                {repeat("ab", 100), "ababababababababababababababab... (long string, length=200)"},
                {repeat("A", 250), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA... (long string, length=250)"},
                {null, "null"},
                {1, "1"},
                {1000, "1000"},
                {1000.1, "1000.1"},
                {UserFieldEnum.EMAIL, "EMAIL"},
                {UserFieldEnum.NICKNAME, "NICKNAME"},
                {LobbyLeagueEnum.COMPETITIVE, "COMPETITIVE"},
                {LobbyLeagueEnum.REGULAR, "REGULAR"}
        };
    }
    
    @Test(dataProvider = "dataProviderShortenLog")
    public void shouldVerifyShortenLog(Object input, Object expected) {
        assertEquals(Tools.shortenLog(input), expected);
    }
    
    @DataProvider(name = "dataProviderShortenAndAnonymizeLog")
    public Object[][] dataProviderShortenAndAnonymizeLog() {
        return new Object[][]{
                {"abc", "***"},
                {repeat("a", 200), "******************************... (long string, length=200)"},
                {repeat("ab", 100), "******************************... (long string, length=200)"},
                {repeat("A", 250), "******************************... (long string, length=250)"},
                {null, "null"},
                {1, "*"},
                {1000, "****"},
                {1000.1, "******"},
                {UserFieldEnum.EMAIL, "*****"},
                {UserFieldEnum.NICKNAME, "********"},
                {LobbyLeagueEnum.COMPETITIVE, "***********"},
                {LobbyLeagueEnum.REGULAR, "*******"}
        };
    }
    
    @Test(dataProvider = "dataProviderShortenAndAnonymizeLog")
    public void shouldVerifyShortenAndAnonymizeLog(Object input, Object expected) {
        assertEquals(Tools.shortenAndAnonymizeLog(input), expected);
    }
}
