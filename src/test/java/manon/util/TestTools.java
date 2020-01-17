package manon.util;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestTools {

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

    /** Convert a duration to a number of days.*/
    @Contract(pure = true)
    public static int days(@NotNull Duration duration) {
        return (int) (duration.getSeconds() / 86_400L);
    }
}
