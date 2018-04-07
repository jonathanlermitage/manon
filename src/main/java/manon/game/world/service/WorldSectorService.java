package manon.game.world.service;

import manon.game.world.document.WorldSector;

import java.util.List;

public interface WorldSectorService {
    
    long count();
    
    List<WorldSector> save(Iterable<WorldSector> sectors);
    
    void delete(Iterable<? extends WorldSector> sectors);
}
