package manon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static manon.util.Tools.repeat;
import static org.assertj.core.api.Assertions.assertThat;

public class ToolsTest {

    private static Object[][] dataProviderShouldVerifyIsBlank() {
        return new Object[][]{
            {null, true},
            {"", true},
            {"   ", true},
            {"a", false}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyIsBlank")
    public void shouldVerifyIsBlank(String input, Object expected) {
        assertThat(Tools.isBlank(input)).isEqualTo(expected);
    }

    public static Object[][] dataProviderShouldVerifyShortenLog() {
        return new Object[][]{
            {"abc", "abc"},
            {repeat("a", 200), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa... (long string, length=200)"},
            {repeat("ab", 100), "ababababababababababababababab... (long string, length=200)"},
            {repeat("A", 250), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA... (long string, length=250)"},
            {null, "null"},
            {1, "1"},
            {1000, "1000"},
            {1000.1, "1000.1"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyShortenLog")
    public void shouldVerifyShortenLog(Object input, Object expected) {
        assertThat(Tools.shortenLog(input)).isEqualTo(expected);
    }

    public static Object[][] dataProviderShouldVerifyShortenAndAnonymizeLog() {
        return new Object[][]{
            {"abc", "***"},
            {repeat("a", 200), "******************************... (long string, length=200)"},
            {repeat("ab", 100), "******************************... (long string, length=200)"},
            {repeat("A", 250), "******************************... (long string, length=250)"},
            {null, "null"},
            {1, "*"},
            {1000, "****"},
            {1000.1, "******"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyShortenAndAnonymizeLog")
    public void shouldVerifyShortenAndAnonymizeLog(Object input, Object expected) {
        assertThat(Tools.shortenAndAnonymizeLog(input)).isEqualTo(expected);
    }

    public static Object[][] dataProviderShouldVerifyRepeat() {
        return new Object[][]{
            {null, -1, null},
            {null, 0, null},
            {null, 1, null},
            {null, 2, null},
            {"", -1, ""},
            {"", 0, ""},
            {"", 1, ""},
            {"", 2, ""},
            {"a", -1, ""},
            {"a", 0, ""},
            {"a", 1, "a"},
            {"a", 2, "aa"},
            {"abc", 0, ""},
            {"abc", -1, ""},
            {"abc", 1, "abc"},
            {"abc", 2, "abcabc"},
            {" a b c ", -1, ""},
            {" a b c ", 0, ""},
            {" a b c ", 1, " a b c "},
            {" a b c ", 2, " a b c  a b c "}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyRepeat")
    public void shouldVerifyRepeat(String str, Integer repeat, String expected) {
        assertThat(repeat(str, repeat)).isEqualTo(expected);
    }
}
