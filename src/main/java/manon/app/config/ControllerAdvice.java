package manon.app.config;

import com.mongodb.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.profile.ProfileNotFoundException;
import manon.profile.ProfileUpdateFormException;
import manon.profile.friendship.FriendshipExistsException;
import manon.profile.friendship.FriendshipRequestExistsException;
import manon.profile.friendship.FriendshipRequestNotFoundException;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.UserPasswordUpdateFormException;
import manon.user.registration.RegistrationFormException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    
    public static final String FIELD_ERRORS = "errors";
    public static final String FIELD_MESSAGE = "errorMessage";
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(MethodArgumentNotValidException error) {
        List<FieldError> errors = error.getBindingResult().getFieldErrors();
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, errors);
        map.put(FIELD_MESSAGE, error.getMessage());
        return map;
    }
    
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(UserExistsException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getUsername());
        return map;
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(UserNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getId() != null) {
            map.put(FIELD_MESSAGE, error.getId());
        }
        return map;
    }
    
    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(ProfileNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getIds() != null) {
            map.put(FIELD_MESSAGE, error.getIds());
        }
        return map;
    }
    
    @ExceptionHandler(RegistrationFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(RegistrationFormException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
        return map;
    }
    
    @ExceptionHandler(ProfileUpdateFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(ProfileUpdateFormException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
        return map;
    }
    
    @ExceptionHandler(UserPasswordUpdateFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handle(UserPasswordUpdateFormException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        if (error.getErrors() != null && !error.getErrors().isEmpty()) {
            map.put(FIELD_MESSAGE, error.getErrors().stream().map(DefaultMessageSourceResolvable::getCode).collect(Collectors.toList()));
        }
        return map;
    }
    
    @ExceptionHandler(FriendshipRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getProfileIdFrom(), error.getProfileIdTo()});
        return map;
    }
    
    @ExceptionHandler(FriendshipExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipExistsException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getProfileIdFrom(), error.getProfileIdTo()});
        return map;
    }
    
    @ExceptionHandler(FriendshipRequestExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(FriendshipRequestExistsException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getProfileIdFrom(), error.getProfileIdTo()});
        return map;
    }
    
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(DuplicateKeyException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getMessage());
        return map;
    }
    
    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getProfileIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamLeaderOnlyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamLeaderOnlyException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getProfileIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamMemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamMemberNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getProfileIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamInvitationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamInvitationNotFoundException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getTeamInvitationId());
        return map;
    }
    
    @ExceptionHandler(TeamFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(TeamFullException error) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getTeamId());
        return map;
    }
}
