package manon.matchmaking.model;

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
public class LobbyStatus {
    
    public static final LobbyStatus EMPTY = new LobbyStatus();
    
    private LobbySolo lobbySolo;
    private LobbyTeam lobbyTeam;
}
