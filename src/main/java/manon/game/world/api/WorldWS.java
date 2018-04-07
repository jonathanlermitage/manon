package manon.game.world.api;

import lombok.RequiredArgsConstructor;
import manon.game.world.err.WorldNotFoundException;
import manon.game.world.model.WorldSummary;
import manon.game.world.service.WorldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static manon.app.config.API.API_WORLD;

@RestController
@RequestMapping(value = API_WORLD)
@RequiredArgsConstructor
public class WorldWS {
    
    private final WorldService worldService;
    
    @GetMapping("/summary/{id}")
    public WorldSummary readWorldView(@PathVariable("id") String id)
            throws WorldNotFoundException {
        return worldService.readWorldSummary(id);
    }
    
    @GetMapping("/summary/all")
    public List<WorldSummary> findAllWorldViews() {
        return worldService.findAllWorldSummaries();
    }
}
