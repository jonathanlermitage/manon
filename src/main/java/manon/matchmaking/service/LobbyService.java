package manon.matchmaking.service;

import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.LobbyStatus;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.document.TeamInvitation;
import manon.user.UserNotFoundException;

import java.util.List;

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
    void enter(String userId, LobbyLeagueEnum league);
    
    /**
     * Remove a user from the lobby.
     * If user is a team leader, another team member is promoted.
     * If team gets empty, it's deleted.
     * @param userId user id.
     */
    void quit(String userId);
    
    /**
     * Create a team and enter into.
     * @param userId user id.
     */
    LobbyTeam createTeamAndEnter(String userId, LobbyLeagueEnum league);
    
    /**
     * Invite a user to a team.
     * @param userId user id that is in the team.
     * @param userIdToInvite user id to invite.
     */
    TeamInvitation inviteToTeam(String userId, String userIdToInvite)
            throws TeamNotFoundException, TeamInvitationException, UserNotFoundException;
    
    /**
     * Find a user pending invitations to teams.
     * @param userId user id.
     * @return team invitations.
     */
    List<TeamInvitation> getTeamInvitations(String userId);
    
    /**
     * Add a user into a team to the lobby.
     * @param userId user id.
     * @param invitationId team invitation id.
     */
    LobbyTeam acceptTeamInvitation(String userId, String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException;
    
    /**
     * Get a team.
     * @param userId user id.
     */
    LobbyTeam getTeam(String userId) throws TeamNotFoundException;
    
    /**
     * Mark a team as ready. Only team leader can do that.
     * @param userId user id.
     * @param ready ready.
     */
    LobbyTeam markTeamReady(String userId, boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException;
    
    /**
     * Set a new leader for current team. Only team leader can do that.
     * @param userId user id of current leader.
     * @param newLeaderUserId user id of new leader.
     */
    LobbyTeam setTeamLeader(String userId, String newLeaderUserId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException;
}
