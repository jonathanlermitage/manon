package manon.util;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;

import java.time.Clock;
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

    /** Logback MDC keys. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Mdc {
        /** {@value} MDC environment key. */
        public static final String KEY_ENV = "env";
        /** {@value} MDC correlation id key. */
        public static final String KEY_CORRELATION_ID = "reqId";
        /** {@value} MDC user key. */
        public static final String KEY_USER = "user";
        /** {@value} MDC user key. */
        public static final String KEY_URI = "uri";
    }

    /** Media types. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Media {
        /** {@value} media type. */
        public static final String JSON = MediaType.APPLICATION_JSON_VALUE;
        /** {@value} media type. */
        public static final String TEXT = MediaType.TEXT_PLAIN_VALUE;
    }

    public static final String ZONE_ID_NAME = "Europe/Paris";
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_NAME);
    public static final Clock CLOCK = Clock.system(ZONE_ID);

    /** Get current date as {@code java.time.LocalDateTime} from {@value ZONE_ID_NAME} timezone. */
    @NotNull
    public static LocalDateTime now() {
        return truncate(ZonedDateTime.now(ZONE_ID).toLocalDateTime().truncatedTo(ChronoUnit.MICROS));
    }

    /** Get current date as {@code java.util.Date} from {@value ZONE_ID_NAME} timezone. */
    @NotNull
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

    public static LocalDateTime truncate(LocalDateTime ldt) {
        return ldt.truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * Get today's date with given hours, minutes, seconds and milliseconds.
     * @param hourOfDay hours (0-23).
     * @param minute minutes (0-59).
     * @param second seconds (0-59).
     * @param millisecond milliseconds (0-999).
     */
    @NotNull
    public static LocalDateTime today(int hourOfDay, int minute, int second, int millisecond) {
        return now().withHour(hourOfDay).withMinute(minute).withSecond(second).withNano(millisecond * 1_000_000);
    }

    /** Get current date at 0:00'0 000. */
    @NotNull
    public static LocalDateTime startOfDay() {
        return today(0, 0, 0, 0);
    }

    /** Get current date at 23:59'59 999. */
    @NotNull
    public static LocalDateTime endOfDay() {
        return today(23, 59, 59, 999);
    }

    /** Return {@code true} if the provided string is null or empty once trimmed, otherwise {@code false}. */
    @Contract("null -> true")
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Contract("null -> !null")
    public static String shortenLog(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj instanceof String ? ((String) obj) : obj.toString();
        if (str.length() > 100) {
            return str.substring(0, 30) + "... (long string, length=" + str.length() + ")";
        }
        return str;
    }

    @Contract("null -> !null")
    public static String shortenAndAnonymizeLog(Object obj) {
        if (obj == null) {
            return "null";
        }
        String str = obj instanceof String ? ((String) obj) : obj.toString();
        if (str.length() > 100) {
            return "*".repeat(30) + "... (long string, length=" + str.length() + ")";
        }
        return "*".repeat(str.length());
    }
}
