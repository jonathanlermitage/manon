package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.model.LobbyLeague;
import manon.matchmaking.model.LobbyStatus;
import manon.matchmaking.service.LobbyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_LOBBY;

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
        log.debug("user {} gets his lobby status", user.getIdentity());
        return lobbyService.getStatus(user.getUserId());
    }
    
    /**
     * Remove a user from the lobby.
     * @param user user.
     */
    @PutMapping(value = "/quit")
    public void quit(@AuthenticationPrincipal UserSimpleDetails user) {
        log.debug("user {} quits lobby", user.getIdentity());
        lobbyService.quit(user.getUserId());
    }
    
    /**
     * Add a user to the lobby.
     * @param user user.
     */
    @PutMapping(value = "/enter/{league}")
    public void enter(@AuthenticationPrincipal UserSimpleDetails user,
                      @PathVariable("league") LobbyLeague league) {
        log.debug("user {} enters in lobby", user.getIdentity());
        lobbyService.enter(user.getUserId(), league);
    }
}
