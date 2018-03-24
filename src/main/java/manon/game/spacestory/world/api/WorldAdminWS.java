package manon.game.spacestory.world.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.game.spacestory.world.WorldExistsException;
import manon.game.spacestory.world.WorldNotFoundException;
import manon.game.spacestory.world.form.WorldRegistrationForm;
import manon.game.spacestory.world.service.WorldService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_WORLD_ADMIN;
import static manon.util.Tools.MEDIA_JSON;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(value = API_WORLD_ADMIN)
@RequiredArgsConstructor
@Slf4j
public class WorldAdminWS {
    
    private final WorldService worldService;
    
    @PostMapping(consumes = MEDIA_JSON)
    @ResponseStatus(CREATED)
    public String register(@AuthenticationPrincipal UserSimpleDetails admin,
                           @RequestBody @Validated WorldRegistrationForm registrationForm)
            throws WorldExistsException {
        log.info("admin {} registers new world {}", admin.getUsername(), registrationForm);
        return worldService.register(registrationForm);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal UserSimpleDetails admin,
                       @PathVariable("id") String id)
            throws WorldNotFoundException {
        log.info("admin {} deletes world {}", admin.getUsername(), id);
        worldService.delete(id);
    }
}
