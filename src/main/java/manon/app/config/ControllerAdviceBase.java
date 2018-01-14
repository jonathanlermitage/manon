package manon.app.config;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 * Look for implementations at package level: batch, user, user, etc.
 */
public abstract class ControllerAdviceBase {
    
    public static final String FIELD_ERRORS = "errors";
    public static final String FIELD_MESSAGE = "errorMessage";
    
    protected Map<String, Object> error() {
        return new HashMap<>();
    }
}
