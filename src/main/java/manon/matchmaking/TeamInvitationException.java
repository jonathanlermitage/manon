package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamInvitationException extends Exception {
    
    private final Cause errorCause;
    
    public enum Cause {
        INVITE_ITSELF
    }
}
