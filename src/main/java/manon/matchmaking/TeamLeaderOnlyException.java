package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamLeaderOnlyException extends Exception {
    
    private String profileIdMember;
}
