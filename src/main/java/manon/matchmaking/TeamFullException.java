package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamFullException extends Exception {
    
    private final String teamId;
}
