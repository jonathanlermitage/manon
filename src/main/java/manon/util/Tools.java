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
    public static final String MDC_KEY_REQUEST_ID = "reqId";
    public static final String MDC_KEY_USER = "user";
    
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
    
    public static Date yesterday() {
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        return yesterday.getTime();
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
    
    /** Get a new MongoDB ObjectId as String. */
    public static String objId() {
        return new ObjectId().toString();
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
    
    private Tools() {
        // utility class
    }
}
