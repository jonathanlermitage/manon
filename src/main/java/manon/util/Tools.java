package manon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

/**
 * Utility methods related to date and time.
 */
@NoArgsConstructor(access = PRIVATE)
public final class Tools {
    
    public static final String MDC_KEY_ENV = "env";
    public static final String MDC_KEY_REQUEST_ID = "reqId";
    public static final String MDC_KEY_USER = "user";
    
    /** {@value} media type. */
    public static final String MEDIA_JSON = "application/json";
    
    public static final String ZONE_ID_NAME = "Europe/Paris";
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_NAME);
    
    public static final ObjectMapper JSON = new ObjectMapper();
    
    /** Get current date as {@code java.time.LocalDateTime} from {@value ZONE_ID_NAME} timezone. */
    public static LocalDateTime now() {
        return ZonedDateTime.now(ZONE_ID).toLocalDateTime().truncatedTo(ChronoUnit.MICROS);
    }
    
    /** Get current date as {@code java.util.Date} from {@value ZONE_ID_NAME} timezone. */
    public static Date nowAsDate() {
        return Date.from(Instant.from(ZonedDateTime.now(Tools.ZONE_ID)));
    }
    
    public static LocalDateTime yesterday() {
        return nowMinusDays(1);
    }
    
    public static LocalDateTime tomorrow() {
        return nowPlusDays(1);
    }
    
    public static LocalDateTime nowMinusDays(int nbDays) {
        return now().minus(Period.ofDays(nbDays));
    }
    
    public static LocalDateTime nowPlusDays(int nbDays) {
        return now().plus(Period.ofDays(nbDays));
    }
    
    /**
     * Get today's date with given hours, minutes, seconds and milliseconds.
     * @param hourOfDay hours (0-23).
     * @param minute minutes (0-59).
     * @param second seconds (0-59).
     * @param millisecond milliseconds (0-999).
     */
    public static LocalDateTime today(int hourOfDay, int minute, int second, int millisecond) {
        return now().withHour(hourOfDay).withMinute(minute).withSecond(second).withNano(millisecond * 1_000_000);
    }
    
    /** Get current date at 0:00'0 000. */
    public static LocalDateTime startOfDay() {
        return today(0, 0, 0, 0);
    }
    
    /** Get current date at 23:59'59 999. */
    public static LocalDateTime endOfDay() {
        return today(23, 59, 59, 999);
    }
    
    /** Return {@code true} if the provided string is null or empty once trimmed, otherwise {@code false}. */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static String shortenLog(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj.toString();
        if (str.length() > 100) {
            return str.substring(0, 30) + "... (long string, length=" + str.length() + ")";
        }
        return str;
    }
    
    public static String shortenAndAnonymizeLog(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj.toString();
        if (str.length() > 100) {
            return repeat("*", 30) + "... (long string, length=" + str.length() + ")";
        }
        return repeat("*", str.length());
    }
    
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        } else if (repeat <= 0) {
            return "";
        } else if (repeat == 1) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < repeat; i++) {
                sb.append(str);
            }
            return sb.toString();
        }
    }
    
    /**
     * Wait 10ms.
     * Some service invocations are too fast to introduce time between them: use this method to wait 10ms.
     */
    @ExistForTesting
    @SneakyThrows(InterruptedException.class)
    public static void temporize() {
        Thread.sleep(10);
    }
}
