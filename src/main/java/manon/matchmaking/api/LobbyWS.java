package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.service.LobbyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_V1;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/** Matchmaking API. */
@RestController
@RequestMapping(value = API_V1 + API_LOBBY)
@RequiredArgsConstructor
@Slf4j
public class LobbyWS {
    
    private final LobbyService lobbyService;
    
    /**
     * Indicate if a profile is in the lobby already, and where.
     * @param user user.
     */
    @RequestMapping(value = "/status", method = GET)
    public ProfileLobbyStatus getStatus(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} gets his lobby status", user.getIdentity());
        return lobbyService.getStatus(user.getProfileId());
    }
    
    /**
     * Remove a profile from the lobby.
     * @param user user.
     */
    @RequestMapping(value = "/quit", method = PUT)
    public void quit(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} quits lobby", user.getIdentity());
        lobbyService.quit(user.getProfileId());
    }
}
