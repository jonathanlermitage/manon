package manon.app.config;

import com.mongodb.DuplicateKeyException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@ControllerAdvice
public class SpringControllerAdvice extends ControllerAdviceBase {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(MethodArgumentNotValidException error) {
        List<FieldError> errors = error.getBindingResult().getFieldErrors();
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, errors);
        map.put(FIELD_MESSAGE, error.getMessage());
        return map;
    }
    
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(DuplicateKeyException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getMessage());
        return map;
    }
}
