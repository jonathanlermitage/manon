package manon.matchmaking.service;

import manon.matchmaking.model.LobbyLeague;
import manon.matchmaking.model.LobbyStatus;

/** Handle lobby. */
public interface LobbyService {
    
    void flush();
    
    /**
     * Indicate if a user is in the lobby already, and where.
     * @param userId user id.
     */
    LobbyStatus getStatus(String userId);
    
    /**
     * Add a user to the lobby.
     * @param userId user id.
     */
    void enter(String userId, LobbyLeague league);
    
    /**
     * Remove a user from the lobby.
     * @param userId user id.
     */
    void quit(String userId);
}
