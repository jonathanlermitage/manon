package manon.matchmaking.service;

import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbyTeam;

/** Handle lobby. */
public interface LobbyService {
    
    /**
     * Indicate if a profile is in the lobby already, and where.
     * @param profileId profile id.
     */
    ProfileLobbyStatus getStatus(String profileId);
    
    /**
     * Add a profile to the lobby.
     * @param profileId profile id.
     */
    void enter(String profileId, LobbyLeagueEnum league);
    
    /**
     * Remove a profile from the lobby.
     * @param profileId profile id.
     */
    void quit(String profileId);
    
    /**
     * Create a team.
     * @param profileId profile id.
     */
    LobbyTeam createTeam(String profileId, LobbyLeagueEnum league);
    
    /**
     * Invite a profile to a team.
     * @param profileId profile id to invite.
     * @param teamId team id.
     */
    LobbyTeam inviteToTeam(String profileId, String teamId)
            throws TeamNotFoundException;
    
    /**
     * Add a profile to a team.
     * @param profileId profile id to add.
     * @param teamId team id.
     */
    LobbyTeam enterIntoTeam(String profileId, String teamId)
            throws TeamNotFoundException, TeamFullException;
    
    /**
     * Add a profiles into a team to the lobby.
     * @param profileId profile id.
     * @param invitationId team id.
     */
    LobbyTeam acceptTeamInvitation(String profileId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException;
    
    /**
     * Get a team.
     * @param profileId profile id.
     */
    LobbyTeam getTeamByProfile(String profileId)
            throws TeamNotFoundException;
    
    /**
     * Mark a team as ready. Only team leader can do that.
     * @param profileId profile id.
     * @param ready ready.
     */
    LobbyTeam markTeamReady(String profileId, boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException;
    
    /**
     * Set a new leader for current team. Only team leader can do that.
     * @param profileId profile id of current leader.
     * @param newLeaderProfileId profile id of new leader.
     */
    LobbyTeam changeTeamLeader(String profileId, String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException;
}
