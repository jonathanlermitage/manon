package manon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility methods related to now and time.
 */
public final class Tools {
    
    public static final String MDC_KEY_ENV = "env";
    public static final String MDC_KEY_REQUEST_ID = "r";
    public static final String MDC_KEY_USER = "u";
    
    /** {@value} media type. */
    public static final String MEDIA_JSON = "application/json";
    
    /** {@value} date format. */
    public static final String DATE_FORMAT = "yyyy-MM-ddHH:mm:ssS Z";
    
    public static final ObjectMapper JSON;
    
    static {
        JSON = new ObjectMapper();
        JSON.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
    }
    
    /** Get current date. */
    public static Date now() {
        return new Date();
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
    
    /** Get a new MongoDB ObjectId as String. */
    public static String objId() {
        return new ObjectId().toString();
    }
}
