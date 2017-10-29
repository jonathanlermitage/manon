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
import manon.matchmaking.document.LobbyTeam;
import manon.matchmaking.document.TeamInvitation;
import manon.matchmaking.service.LobbyService;
import manon.profile.ProfileNotFoundException;
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
import static manon.app.config.API.API_V1;
import static manon.util.Tools.MEDIA_JSON;
import static org.springframework.http.HttpStatus.CREATED;

/** Matchmaking API. */
@RestController
@RequestMapping(value = API_V1 + API_LOBBY)
@RequiredArgsConstructor
@Slf4j
public class LobbyTeamWS {
    
    private final LobbyService lobbyService;
    
    /**
     * Create a team and enter into it.
     * @param user user.
     */
    @PostMapping(value = "/team/{league}", consumes = MEDIA_JSON)
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
    @PutMapping(value = "/invite/profile/{profileId}/team")
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
    @GetMapping(value = "/team/invitations")
    public List<TeamInvitation> getTeamInvitations(@AuthenticationPrincipal UserSimpleDetails user) {
        return lobbyService.getTeamInvitations(user.getProfileId());
    }
    
    /**
     * Add a profiles into a team to the lobby.
     * @param user user.
     * @param invitationId team invitation id.
     */
    @PutMapping(value = "/accept/team/invitation/{invitationId}")
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
    @GetMapping(value = "/team")
    public LobbyTeam getTeam(@AuthenticationPrincipal UserSimpleDetails user)
            throws TeamNotFoundException {
        log.info("user {} gets his team", user.getIdentity());
        return lobbyService.getTeam(user.getProfileId());
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
        return lobbyService.markTeamReady(user.getProfileId(), ready);
    }
    
    /**
     * Set a new leader for current team. Only team leader can do that.
     * @param user team leader.
     * @param newLeaderProfileId profile id of new team leader.
     */
    @PutMapping(value = "/team/leader/{newLeaderProfileId}")
    public LobbyTeam setTeamLeader(@AuthenticationPrincipal UserSimpleDetails user,
                                   @PathVariable("newLeaderProfileId") String newLeaderProfileId)
            throws TeamNotFoundException, TeamLeaderOnlyException, TeamMemberNotFoundException {
        log.info("user {} set profile {} has new team leader", user.getIdentity(), newLeaderProfileId);
        return lobbyService.setTeamLeader(user.getProfileId(), newLeaderProfileId);
    }
}
