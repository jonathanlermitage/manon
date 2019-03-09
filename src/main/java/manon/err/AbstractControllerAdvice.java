package manon.err;

import java.util.Collections;
import java.util.Map;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 */
public abstract class AbstractControllerAdvice {
    
    public static final String ERROR_TYPE = "errorType";
    
    protected Map<String, Object> error(AbstractManagedException e) {
        return Collections.singletonMap(ERROR_TYPE, e.getErrorType());
    }
}
