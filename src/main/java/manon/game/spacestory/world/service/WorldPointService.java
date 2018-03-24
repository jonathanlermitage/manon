package manon.game.spacestory.world.service;

import manon.game.spacestory.world.document.WorldPoint;

import java.util.List;

public interface WorldPointService {
    
    long count();
    
    List<WorldPoint> save(Iterable<WorldPoint> points);
    
    void delete(Iterable<? extends WorldPoint> points);
}
