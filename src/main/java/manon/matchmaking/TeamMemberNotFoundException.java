package manon.matchmaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TeamMemberNotFoundException extends Exception {
    
    private final String userIdMember;
}
