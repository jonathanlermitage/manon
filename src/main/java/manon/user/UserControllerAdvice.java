package manon.user;

import manon.app.config.ControllerAdviceBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class UserControllerAdvice extends ControllerAdviceBase {
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(UserNotFoundException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getId());
        return map;
    }
    
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(UserExistsException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getUsername());
        return map;
    }
}
