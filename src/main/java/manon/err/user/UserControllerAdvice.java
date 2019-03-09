package manon.err.user;

import manon.err.AbstractControllerAdvice;
import manon.err.AbstractManagedException;
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
    
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleBadRequest(AbstractManagedException error) {
        return error(error);
    }
    
    @ExceptionHandler({
        FriendshipExistsException.class,
        FriendshipRequestExistsException.class,
        UserExistsException.class})
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handleConflict(AbstractManagedException error) {
        return error(error);
    }
    
    @ExceptionHandler({
        FriendshipNotFoundException.class,
        FriendshipRequestNotFoundException.class,
        UserNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(AbstractManagedException error) {
        return error(error);
    }
}
