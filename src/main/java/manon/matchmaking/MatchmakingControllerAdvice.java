package manon.matchmaking;

import manon.app.config.ControllerAdviceBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class MatchmakingControllerAdvice extends ControllerAdviceBase {
    
    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamNotFoundException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getUserIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamLeaderOnlyException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(TeamLeaderOnlyException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getUserIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamMemberNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamMemberNotFoundException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getUserIdMember());
        return map;
    }
    
    @ExceptionHandler(TeamInvitationNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handle(TeamInvitationNotFoundException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, new String[]{error.getUserId(), error.getTeamId()});
        return map;
    }
    
    @ExceptionHandler(TeamFullException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(TeamFullException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getTeamId());
        return map;
    }
    
    @ExceptionHandler(TeamInvitationException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public Map<String, Object> handle(TeamInvitationException error) {
        Map<String, Object> map = error();
        map.put(FIELD_ERRORS, error.getClass().getSimpleName());
        map.put(FIELD_MESSAGE, error.getErrorCause());
        return map;
    }
}
