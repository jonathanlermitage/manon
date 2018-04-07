package manon.user.err;

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
        return map;
    }
    
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(UserExistsException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        return map;
    }
    
    @ExceptionHandler(FriendshipRequestNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestNotFoundException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getUserIdFrom(), error.getUserIdTo()});
        return map;
    }
    
    @ExceptionHandler(FriendshipExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipExistsException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getUserIdFrom(), error.getUserIdTo()});
        return map;
    }
    
    @ExceptionHandler(FriendshipRequestExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestExistsException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getUserIdFrom(), error.getUserIdTo()});
        return map;
    }
}
