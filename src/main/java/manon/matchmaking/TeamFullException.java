package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamFullException extends Exception {
    
    private String teamId;
}
