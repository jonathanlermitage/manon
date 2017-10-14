package manon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.jacksonlombok.JacksonLombokAnnotationIntrospector;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility methods related to now and time.
 */
public final class Tools {
    
    public static final String MEDIA_JSON = "application/json";
    public static final String MEDIA_TEXT = "text/plain";
    public static final String DATE_FORMAT = "yyyy-MM-ddHH:mm:ssS Z";
    public static final ObjectMapper JSON;
    
    static {
        JSON = new ObjectMapper();
        JSON.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
        JSON.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    }
    
    /** Get current date. */
    public static Date now() {
        return new Date();
    }
    
    /** Get current date plus given days. */
    public static Date nowPlusDays(int nbDays) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, nbDays);
        return cal.getTime();
    }
    
    /**
     * Get today's date with given hours, minutes, seconds and milliseconds.
     * @param hourOfDay hours (0-23).
     * @param minute minutes (0-59).
     * @param second seconds (0-59).
     * @param millisecond milliseconds (0-999).
     */
    public static Calendar calendar(int hourOfDay, int minute, int second, int millisecond) {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal;
    }
    
    /** Get current date at 0:00'0 000. */
    public static Date startOfDay() {
        return calendar(0, 0, 0, 0).getTime();
    }
    
    /** Get current date at 23:59'59 999. */
    public static Date endOfDay() {
        return calendar(23, 59, 59, 999).getTime();
    }
    
    /**
     * Return a formatted string using the specified format string and arguments.
     * @param format format.
     * @param args   arguments.
     * @return A formatted string.
     */
    public static String str(String format, Object... args) {
        return String.format(format, args);
    }
    
    /**
     * Generate an easy to remember password.
     * Short and numerical only.
     * @return password.
     */
    public static String easyPassword() {
        return Double.toString(Math.random() * 10_000).replace(".", "");
    }
    
    /**
     * Returns {@code true} if the provided string is null or empty once trimmed.
     * @param str string.
     * @return {@code true} is null/empty, otherwise {@code false}.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Returns {@code true} if any of the provided reference is {@code null} otherwise returns {@code false}.
     * @param objects references to be checked against {@code null}.
     * @return {@code true} if any of the provided reference is {@code null} otherwise {@code false}.
     */
    public static boolean anyNull(Object... objects) {
        if (null == objects) {
            return true;
        }
        for (Object o : objects) {
            if (null == o) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Create a new MongoDB ObjectId.
     * @return ObjectId as String.
     */
    public static String objId() {
        return new ObjectId().toString();
    }
}
