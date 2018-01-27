package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.LobbyStatus;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.document.TeamInvitation;
import manon.matchmaking.service.LobbyService;
import manon.user.UserNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.config.API.API_LOBBY;
import static org.springframework.http.HttpStatus.CREATED;

/** Matchmaking API. */
@RestController
@RequestMapping(value = API_LOBBY)
@RequiredArgsConstructor
@Slf4j
public class LobbyWS {
    
    private final LobbyService lobbyService;
    
    /**
     * Indicate if a user is in the lobby already, and where.
     * @param user user.
     */
    @GetMapping(value = "/status")
    public LobbyStatus getStatus(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} gets his lobby status", user.getIdentity());
        return lobbyService.getStatus(user.getUserId());
    }
    
    /**
     * Remove a user from the lobby.
     * @param user user.
     */
    @PutMapping(value = "/quit")
    public void quit(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} quits lobby", user.getIdentity());
        lobbyService.quit(user.getUserId());
    }
    
    // Lobby solo
    
    /**
     * Add a user to the lobby.
     * @param user user.
     */
    @PutMapping(value = "/enter/{league}")
    public void enter(@AuthenticationPrincipal UserSimpleDetails user,
                      @PathVariable("league") LobbyLeagueEnum league) {
        log.info("user {} enters in lobby", user.getIdentity());
        lobbyService.enter(user.getUserId(), league);
    }
    
    // Lobby team
    
    /**
     * Create a team and enter into it.
     * @param user user.
     */
    @PostMapping(value = "/team/{league}")
    @ResponseStatus(CREATED)
    public LobbyTeam createTeamAndEnter(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("league") LobbyLeagueEnum league) {
        log.info("user {} creates team", user.getIdentity());
        return lobbyService.createTeamAndEnter(user.getUserId(), league);
    }
    
    /**
     * Invite a user to a team.
     * @param user user.
     * @param userId user id to invite.
     */
    @PutMapping(value = "/invite/user/{userId}/team")
    public TeamInvitation inviteToTeam(@AuthenticationPrincipal UserSimpleDetails user,
                                       @PathVariable("userId") String userId)
            throws TeamNotFoundException, TeamInvitationException, UserNotFoundException {
        log.info("user {} invites user {} into his team", user.getIdentity(), userId);
        return lobbyService.inviteToTeam(user.getUserId(), userId);
    }
    
    /**
     * Get teams invitations.
     * @param user user who may be invited by teams.
     * @return invitations.
     */
    @GetMapping(value = "/team/invitations")
    public List<TeamInvitation> getTeamInvitations(@AuthenticationPrincipal UserSimpleDetails user) {
        return lobbyService.getTeamInvitations(user.getUserId());
    }
    
    /**
     * Add a users into a team to the lobby.
     * @param user user.
     * @param invitationId team invitation id.
     */
    @PutMapping(value = "/accept/team/invitation/{invitationId}")
    public LobbyTeam acceptTeamInvitation(@AuthenticationPrincipal UserSimpleDetails user,
                                          @PathVariable("invitationId") String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException {
        log.info("user {} accepts team invitation {}", user.getIdentity(), invitationId);
        return lobbyService.acceptTeamInvitation(user.getUserId(), invitationId);
    }
    
    /**
     * Get current team.
     * @param user user.
     */
    @GetMapping(value = "/team")
    public LobbyTeam getTeam(@AuthenticationPrincipal UserSimpleDetails user)
            throws TeamNotFoundException {
        log.info("user {} gets his team", user.getIdentity());
        return lobbyService.getTeam(user.getUserId());
    }
    
    /**
     * Mark current team as ready or not ready. Only team leader can do that.
     * @param user team leader.
     * @param ready ready.
     */
    @PutMapping(value = "/team/ready/{ready}")
    public LobbyTeam markTeamReady(@AuthenticationPrincipal UserSimpleDetails user,
                                   @PathVariable("ready") boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException {
        log.info("user {} marks his team ready: {}", user.getIdentity(), ready);
        return lobbyService.markTeamReady(user.getUserId(), ready);
    }
    
    /**
     * Set a new leader for current team. Only team leader can do that.
     * @param user team leader.
     * @param newLeaderUserId user id of new team leader.
     */
    @PutMapping(value = "/team/leader/{newLeaderUserId}")
    public LobbyTeam setTeamLeader(@AuthenticationPrincipal UserSimpleDetails user,
                                   @PathVariable("newLeaderUserId") String newLeaderUserId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        log.info("user {} set user {} has new team leader", user.getIdentity(), newLeaderUserId);
        return lobbyService.setTeamLeader(user.getUserId(), newLeaderUserId);
    }
}
