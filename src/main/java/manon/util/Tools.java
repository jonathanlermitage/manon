package manon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.jacksonlombok.JacksonLombokAnnotationIntrospector;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    /**
     * Get current date.
     * @return current date.
     */
    public static Date now() {
        return new Date();
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
