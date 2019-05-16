package manon.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestTools {
    
    private static final int TRIAL_MIN_CLOCK_RESOLUTION_MS = 2;
    
    /**
     * Append {@code word} many times in order to create a {@code length} characters string; last {@code word} may be
     * truncated to fit {@code length}.
     */
    @NotNull
    public static String fill(String word, int length) {
        StringBuilder sb = new StringBuilder(word);
        while (sb.length() < length) {
            sb.append(word);
        }
        return sb.length() == length ? sb.toString() : sb.toString().substring(0, length);
    }
    
    /**
     * Wait {@value TRIAL_MIN_CLOCK_RESOLUTION_MS} ms.
     * Some service invocations are too fast to introduce time greater than clock's resolution (also, leap
     * seconds may be ignored): use this method to wait {@value TRIAL_MIN_CLOCK_RESOLUTION_MS} ms. Clock's resolution depends on the
     * underlying operating system and may be larger, but this value seems reasonable. It could be increased if needed, but
     * keep in mind that unit tests should remain fast.
     * <p>
     * Typical usage: you persist database items quickly, then you retrieve a list of items ordered by creation date. With no
     * minimal intermission, you may observe that some items have identical creation timestamp, that may introduce lack of order.
     * Adding a very short pause solves this problem. Please note this is a test scenario, production is usually not impacted:
     * that's why this method is limited to the test scope.
     *
     */
    @SneakyThrows(InterruptedException.class)
    public static void temporize() {
        Thread.sleep(TRIAL_MIN_CLOCK_RESOLUTION_MS);
    }
}
