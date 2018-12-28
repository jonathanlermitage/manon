package manon.app.config;

import manon.user.err.AbstractFriendshipException;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 */
public abstract class AbstractControllerAdvice {
    
    public static final String FIELD_ERRORS = "errors";
    public static final String FIELD_MESSAGE = "errorMessage";
    
    protected Map<String, Object> error() {
        return new HashMap<>();
    }
    
    protected Map<String, Object> error(Exception e) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, e.getClass().getSimpleName());
        return map;
    }
    
    protected Map<String, Object> error(AbstractFriendshipException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getUserIdFrom(), error.getUserIdTo()});
        return map;
    }
}
