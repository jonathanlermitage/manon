package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamLeaderOnlyException extends Exception {
    
    private final String userIdMember;
}
