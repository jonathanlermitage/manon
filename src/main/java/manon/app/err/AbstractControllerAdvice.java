package manon.app.err;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 */
public abstract class AbstractControllerAdvice {
    
    public static final String FIELD_ERRORS = "errors";
    
    protected Map<String, Object> error() {
        return new HashMap<>();
    }
    
    protected Map<String, Object> error(Exception e) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, e.getClass().getSimpleName());
        return map;
    }
}
