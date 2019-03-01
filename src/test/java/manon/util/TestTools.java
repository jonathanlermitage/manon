package manon.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestTools {
    
    @NotNull
    public static String fill(String word, int length) {
        StringBuilder sb = new StringBuilder(word);
        while (sb.length() < length) {
            sb.append(word);
        }
        return sb.length() == length ? sb.toString() : sb.toString().substring(0, length);
    }
    
    /**
     * Wait 10ms.
     * Some service invocations are too fast to introduce time between them: use this method to wait 10ms.
     */
    @SneakyThrows(InterruptedException.class)
    public static void temporize() {
        Thread.sleep(10);
    }
}
