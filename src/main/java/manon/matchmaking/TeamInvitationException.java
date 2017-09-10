package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TeamInvitationException extends Exception {
    
    private Cause errorCause;
    
    public enum Cause {
        INVITE_ITSELF
    }
}
