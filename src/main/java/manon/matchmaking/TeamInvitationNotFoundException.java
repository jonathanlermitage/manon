package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamInvitationNotFoundException extends Exception {
    
    private final String userId;
    private final String teamId;
}
