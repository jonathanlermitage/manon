package manon.user.err;

import manon.app.err.AbstractControllerAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class UserControllerAdvice extends AbstractControllerAdvice {
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(UserNotFoundException error) {
        return error(error);
    }
    
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(UserExistsException error) {
        return error(error);
    }
    
    @ExceptionHandler(FriendshipNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(FriendshipNotFoundException error) {
        return error(error);
    }
    
    @ExceptionHandler(FriendshipRequestNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestNotFoundException error) {
        return error(error);
    }
    
    @ExceptionHandler(FriendshipExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipExistsException error) {
        return error(error);
    }
    
    @ExceptionHandler(FriendshipRequestExistsException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestExistsException error) {
        return error(error);
    }
    
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(PasswordNotMatchException error) {
        return error(error);
    }
}
