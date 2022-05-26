package manon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class ToolsTest {

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
    void shouldVerifyIsBlank(String input, Object expected) {
        assertThat(Tools.isBlank(input)).isEqualTo(expected);
    }

    static Object[][] dataProviderShouldVerifyShortenLog() {
        return new Object[][]{
            {"abc", "abc"},
            {"a".repeat(200), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa... (long string, length=200)"},
            {"ab".repeat(100), "ababababababababababababababab... (long string, length=200)"},
            {"A".repeat(250), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA... (long string, length=250)"},
            {null, "null"},
            {1, "1"},
            {1000, "1000"},
            {1000.1, "1000.1"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyShortenLog")
    void shouldVerifyShortenLog(Object input, Object expected) {
        assertThat(Tools.shortenLog(input)).isEqualTo(expected);
    }

    static Object[][] dataProviderShouldVerifyShortenAndAnonymizeLog() {
        return new Object[][]{
            {"abc", "***"},
            {"a".repeat(200), "******************************... (long string, length=200)"},
            {"ab".repeat(100), "******************************... (long string, length=200)"},
            {"A".repeat(250), "******************************... (long string, length=250)"},
            {null, "null"},
            {1, "*"},
            {1000, "****"},
            {1000.1, "******"}
        };
    }

    @ParameterizedTest
    @MethodSource("dataProviderShouldVerifyShortenAndAnonymizeLog")
    void shouldVerifyShortenAndAnonymizeLog(Object input, Object expected) {
        assertThat(Tools.shortenAndAnonymizeLog(input)).isEqualTo(expected);
    }
}
