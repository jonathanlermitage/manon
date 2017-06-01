package manon.profile;

/**
 * Profile states.
 */
public enum ProfileStateEnum {
    
    /** In the lobby as a single player. */
    IN_LOBBY_SINGLE,
    
    /** In the lobby as a team player. */
    IN_LOBBY_TEAM,
    
    /** Playing single vs an AI. */
    PLAYING_SINGLE_VS_AI,
    
    /** Playing in a 1v1 game. */
    PLAYING_SINGLE,
    
    /** Playing in a teamed game. */
    PLAYING_TEAM,
    
    /** Connected. */
    CONNECTED,
    
    /** Disconnected or idle since a long time. */
    DISCONNECTED_OR_IDLE
}
