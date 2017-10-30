package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamInvitationException extends Exception {
    
    private Cause errorCause;
    
    public enum Cause {
        INVITE_ITSELF
    }
}
