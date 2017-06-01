package manon.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.matchmaking.document.LobbySolo;
import manon.matchmaking.document.LobbyTeam;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class ProfileLobbyStatus {
    
    public static final ProfileLobbyStatus EMPTY = new ProfileLobbyStatus();
    
    private LobbySolo lobbySolo;
    private LobbyTeam lobbyTeam;
}
