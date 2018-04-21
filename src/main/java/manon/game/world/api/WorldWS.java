package manon.game.world.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.game.world.err.WorldNotFoundException;
import manon.game.world.model.WorldSummary;
import manon.game.world.service.WorldService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.config.API.API_WORLD;

@RestController
@RequestMapping(value = API_WORLD)
@RequiredArgsConstructor
@Slf4j
public class WorldWS {
    
    private final WorldService worldService;
    
    @GetMapping("/summary/{id}")
    public WorldSummary readWorldView(@AuthenticationPrincipal UserSimpleDetails user,
                                      @PathVariable("id") String id)
            throws WorldNotFoundException {
        log.debug("user {} reads world view {}", user.getIdentity(), id);
        return worldService.readWorldSummary(id);
    }
    
    @GetMapping("/summary/all")
    public List<WorldSummary> findAllWorldViews(@AuthenticationPrincipal UserSimpleDetails user) {
        log.debug("user {} reads all world views", user.getIdentity());
        return worldService.findAllWorldSummaries();
    }
}
