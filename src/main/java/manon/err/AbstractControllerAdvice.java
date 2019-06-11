package manon.err;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} helper.
 */
public interface AbstractControllerAdvice {
    
    default ErrorResponse error(AbstractManagedException e) {
        return new ErrorResponse(e.getErrorType());
    }
}
