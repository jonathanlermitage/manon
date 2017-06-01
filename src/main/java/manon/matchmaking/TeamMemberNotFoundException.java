package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TeamMemberNotFoundException extends Exception {
    
    private String profileIdMember;
}
