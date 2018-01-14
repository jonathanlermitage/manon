package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamNotFoundException extends Exception {
    
    private String userIdMember;
}
