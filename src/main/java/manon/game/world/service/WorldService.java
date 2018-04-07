package manon.game.world.service;

import manon.game.world.document.World;
import manon.game.world.err.WorldExistsException;
import manon.game.world.err.WorldNotFoundException;
import manon.game.world.form.WorldRegistrationForm;
import manon.game.world.model.WorldSummary;

import java.util.List;

public interface WorldService {
    
    void evictCaches();
    
    long count();
    
    /** Create a new world with given number of sectors and points per sector, then add life to it (story, events, entities, etc). */
    World register(WorldRegistrationForm form) throws WorldExistsException;
    
    /**
     * Delete a world and its life.
     * @param id world id.
     */
    void delete(String id) throws WorldNotFoundException;
    
    World read(String id) throws WorldNotFoundException;
    
    WorldSummary readWorldSummary(String id) throws WorldNotFoundException;
    
    List<WorldSummary> findAllWorldSummaries();
}
