package manon.matchmaking.repository;

import manon.matchmaking.document.LobbyTeam;

public interface LobbyTeamRepositoryCustom {
    
    LobbyTeam removeUserId(String id, String userId);
    
    LobbyTeam addUserId(String id, String userId);
    
    void setLeader(String id, String userId);
    
    void setReady(String id, boolean ready);
}
