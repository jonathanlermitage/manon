package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamNotFoundException extends Exception {
    
    private final String userIdMember;
}
