package manon.game.spacestory.world.service;

import manon.game.spacestory.world.WorldExistsException;
import manon.game.spacestory.world.WorldNotFoundException;
import manon.game.spacestory.world.document.World;
import manon.game.spacestory.world.document.WorldSummary;
import manon.game.spacestory.world.form.WorldRegistrationForm;

import java.util.List;

public interface WorldService {
    
    long count();
    
    /** Create a new world with given number of sectors and points per sector, then add life to it (story, events, entities, etc). */
    String register(WorldRegistrationForm form) throws WorldExistsException;
    
    /**
     * Delete a world and its life.
     * @param id world id.
     */
    void delete(String id) throws WorldNotFoundException;
    
    World read(String id) throws WorldNotFoundException;
    
    WorldSummary readWorldSummary(String id) throws WorldNotFoundException;
    
    List<WorldSummary> findAllWorldSummaries();
}
