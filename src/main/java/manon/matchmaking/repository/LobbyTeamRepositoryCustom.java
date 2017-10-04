package manon.matchmaking.repository;

import manon.matchmaking.document.LobbyTeam;

public interface LobbyTeamRepositoryCustom {
    
    LobbyTeam removeProfileId(String id, String profileId);
    
    LobbyTeam addProfileId(String id, String profileId);
    
    void setLeader(String id, String profileId);
    
    void setReady(String id, boolean ready);
}
