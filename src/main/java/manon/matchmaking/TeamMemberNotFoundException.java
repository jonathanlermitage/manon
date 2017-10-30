package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamMemberNotFoundException extends Exception {
    
    private String profileIdMember;
}
