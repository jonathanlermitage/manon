package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.TeamFullException;
import manon.matchmaking.TeamInvitation;
import manon.matchmaking.TeamInvitationException;
import manon.matchmaking.TeamInvitationNotFoundException;
import manon.matchmaking.TeamLeaderOnlyException;
import manon.matchmaking.TeamMemberNotFoundException;
import manon.matchmaking.TeamNotFoundException;
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.service.LobbyService;
import manon.profile.ProfileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_V1;
import static manon.util.Tools.MEDIA_JSON;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/** Matchmaking API. */
@RestController
@RequestMapping(value = API_V1 + API_LOBBY)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class LobbyTeamWS {
    
    private final LobbyService lobbyService;
    
    /**
     * Create a team and enter into it.
     * @param user user.
     */
    @RequestMapping(value = "/team/{league}", method = POST, consumes = MEDIA_JSON)
    @ResponseStatus(CREATED)
    public LobbyTeam createTeamAndEnter(@AuthenticationPrincipal UserSimpleDetails user,
                                        @PathVariable("league") LobbyLeagueEnum league)
            throws TeamFullException, TeamNotFoundException {
        log.info("user {} creates team", user.getIdentity());
        return lobbyService.createTeamAndEnter(user.getProfileId(), league);
    }
    
    /**
     * Invite a profile to a team.
     * @param user user.
     * @param profileId profile id to invite.
     */
    @RequestMapping(value = "/invite/profile/{profileId}/team", method = PUT)
    public TeamInvitation inviteToTeam(@AuthenticationPrincipal UserSimpleDetails user,
                                       @PathVariable("profileId") String profileId)
            throws TeamNotFoundException, TeamInvitationException, ProfileNotFoundException {
        log.info("user {} invites profile {} into team {}", user.getIdentity(), profileId);
        return lobbyService.inviteToTeam(user.getProfileId(), profileId);
    }
    
    /**
     * Get teams invitations.
     * @param user user who may be invited by teams.
     * @return invitations.
     */
    @RequestMapping(value = "/team/invitations", method = GET)
    public List<TeamInvitation> getTeamInvitations(@AuthenticationPrincipal UserSimpleDetails user) {
        return lobbyService.getTeamInvitations(user.getProfileId());
    }
    
    /**
     * Add a profiles into a team to the lobby.
     * @param user user.
     * @param invitationId team invitation id.
     */
    @RequestMapping(value = "/accept/team/invitation/{invitationId}", method = PUT)
    public LobbyTeam acceptTeamInvitation(@AuthenticationPrincipal UserSimpleDetails user,
                                          @PathVariable("invitationId") String invitationId)
            throws TeamInvitationNotFoundException, TeamFullException, TeamNotFoundException {
        log.info("user {} accepts team invitation {}", user.getIdentity(), invitationId);
        return lobbyService.acceptTeamInvitation(user.getProfileId(), invitationId);
    }
    
    /**
     * Get current team.
     * @param user user.
     */
    @RequestMapping(value = "/team", method = GET)
    public LobbyTeam getTeam(@AuthenticationPrincipal UserSimpleDetails user)
            throws TeamNotFoundException {
        log.info("user {} gets his team", user.getIdentity());
        return lobbyService.getTeam(user.getProfileId());
    }
    
    /**
     * Mark current team as ready. Only team leader can do that.
     * @param user team leader.
     * @param ready ready.
     */
    @RequestMapping(value = "/team/ready/{ready}", method = PUT) // TODO unit test
    public LobbyTeam markTeamReady(@AuthenticationPrincipal UserSimpleDetails user,
                                   @PathVariable("ready") boolean ready)
            throws TeamNotFoundException, TeamLeaderOnlyException {
        log.info("user {} marks his team ready: {}", user.getIdentity(), ready);
        return lobbyService.markTeamReady(user.getProfileId(), ready);
    }
    
    /**
     * Set a new leader for current team. Only team leader can do that.
     * @param user team leader.
     * @param newLeaderProfileId profile id of new team leader.
     */
    @RequestMapping(value = "/team/leader/{newLeaderProfileId}", method = PUT) // TODO unit test
    public LobbyTeam changeTeamLeader(@AuthenticationPrincipal UserSimpleDetails user,
                                      @PathVariable("newLeaderProfileId") String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        log.info("user {} set profile {} has new team leader", user.getIdentity(), newLeaderProfileId);
        return lobbyService.changeTeamLeader(user.getProfileId(), newLeaderProfileId);
    }
}
