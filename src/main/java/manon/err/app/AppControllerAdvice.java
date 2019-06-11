package manon.err.app;

import manon.err.AbstractControllerAdvice;
import manon.err.AbstractManagedException;
import manon.err.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class AppControllerAdvice implements AbstractControllerAdvice {
    
    @ExceptionHandler(PingException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleNotFound(AbstractManagedException error) {
        return error(error);
    }
}
