package manon.app.batch.err;

import manon.app.config.ControllerAdviceBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class BatchControllerAdvice extends ControllerAdviceBase {
    
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TaskNotFoundException error) {
        return error(error);
    }
}
