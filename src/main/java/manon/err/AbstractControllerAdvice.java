package manon.err;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 */
public abstract class AbstractControllerAdvice {
    
    public ErrorResponse error(AbstractManagedException e) {
        return new ErrorResponse(e.getErrorType());
    }
}
