package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.ProfileLobbyStatus;
import manon.matchmaking.service.LobbyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_LOBBY;
import static manon.app.config.API.API_V1;

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
    @GetMapping(value = "/status")
    public ProfileLobbyStatus getStatus(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} gets his lobby status", user.getIdentity());
        return lobbyService.getStatus(user.getProfileId());
    }
    
    /**
     * Remove a profile from the lobby.
     * @param user user.
     */
    @PutMapping(value = "/quit")
    public void quit(@AuthenticationPrincipal UserSimpleDetails user) {
        log.info("user {} quits lobby", user.getIdentity());
        lobbyService.quit(user.getProfileId());
    }
}
