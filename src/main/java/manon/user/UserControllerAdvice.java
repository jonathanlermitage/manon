package manon.user;

import manon.app.config.ControllerAdviceBase;
import manon.user.form.UserPasswordUpdateFormException;
import manon.user.form.UserUpdateFormException;
import manon.user.registration.form.RegistrationFormException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
        if (error.getId() != null) {
            map.put(FIELD_MESSAGE, error.getId());
        }
        return map;
    }
    
    @ExceptionHandler(UserUpdateFormException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(UserUpdateFormException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
        return map;
    }
    
    @ExceptionHandler(RegistrationFormException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(RegistrationFormException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
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
    
    @ExceptionHandler(UserPasswordUpdateFormException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(UserPasswordUpdateFormException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
        return map;
    }
}
