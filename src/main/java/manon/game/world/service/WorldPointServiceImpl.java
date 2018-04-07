package manon.game.world.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.game.world.document.WorldPoint;
import manon.game.world.repository.WorldPointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorldPointServiceImpl implements WorldPointService {
    
    private final WorldPointRepository worldPointRepository;
    
    @Override
    public long count() {
        return worldPointRepository.count();
    }
    
    @Override
    public List<WorldPoint> save(Iterable<WorldPoint> points) {
        return worldPointRepository.saveAll(points);
    }
    
    @Override
    public void delete(Iterable<? extends WorldPoint> points) {
        worldPointRepository.deleteAll(points);
    }
}
