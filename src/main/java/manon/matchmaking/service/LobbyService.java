package manon.matchmaking.service;

import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitation;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbyTeam;
import manon.profile.ProfileNotFoundException;

import java.util.List;

/** Handle lobby. */
public interface LobbyService {
    
    void flush();
    
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
     * If profile is a team leader, another team member is promoted.
     * If team gets empty, it's deleted.
     * @param profileId profile id.
     */
    void quit(String profileId);
    
    /**
     * Create a team and enter into.
     * @param profileId profile id.
     */
    LobbyTeam createTeamAndEnter(String profileId, LobbyLeagueEnum league);
    
    /**
     * Invite a profile to a team.
     * @param profileId profile id that is in the team.
     * @param profileIdToInvite profile id to invite.
     */
    TeamInvitation inviteToTeam(String profileId, String profileIdToInvite)
            throws TeamNotFoundException, TeamInvitationException, ProfileNotFoundException;
    
    /**
     * Find a profile pending invitations to teams.
     * @param profileId profile id.
     * @return team invitations.
     */
    List<TeamInvitation> getTeamInvitations(String profileId);
    
    /**
     * Add a profile to a team.
     * @param profileId profile id to add.
     * @param teamId team id.
     */
    LobbyTeam addToTeam(String profileId, String teamId)
            throws TeamNotFoundException, TeamFullException;
    
    /**
     * Add a profiles into a team to the lobby.
     * @param profileId profile id.
     * @param invitationId team invitation id.
     */
    LobbyTeam acceptTeamInvitation(String profileId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException;
    
    /**
     * Get a team.
     * @param profileId profile id.
     */
    LobbyTeam getTeam(String profileId)
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
    LobbyTeam setTeamLeader(String profileId, String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException;
}
