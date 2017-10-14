package manon.matchmaking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.matchmaking.LobbyLeagueEnum;
import manon.matchmaking.service.LobbyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
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
public class LobbySoloWS {
    
    private final LobbyService lobbyService;
    
    /**
     * Add a profile to the lobby.
     * @param user user.
     */
    @PutMapping(value = "/enter/{league}")
    public void enter(@AuthenticationPrincipal UserSimpleDetails user,
                      @PathVariable("league") LobbyLeagueEnum league) {
        log.info("user {} enters in lobby", user.getIdentity());
        lobbyService.enter(user.getProfileId(), league);
    }
}
