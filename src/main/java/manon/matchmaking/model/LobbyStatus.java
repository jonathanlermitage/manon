package manon.matchmaking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.matchmaking.document.LobbySolo;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class LobbyStatus implements Serializable {
    
    private static final long serialVersionUID = 8582871560075272967L;
    
    public static final LobbyStatus EMPTY = new LobbyStatus();
    
    private LobbySolo lobbySolo;
}
