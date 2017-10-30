package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamInvitationNotFoundException extends Exception {
    
    private String profileId;
    private String teamId;
}
