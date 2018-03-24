package manon.game.spacestory.world.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.game.spacestory.world.document.WorldSector;
import manon.game.spacestory.world.repository.WorldSectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorldSectorServiceImpl implements WorldSectorService {
    
    private final WorldSectorRepository worldSectorRepository;
    
    @Override
    public long count() {
        return worldSectorRepository.count();
    }
    
    @Override
    public List<WorldSector> save(Iterable<WorldSector> sectors) {
        return worldSectorRepository.saveAll(sectors);
    }
    
    @Override
    public void delete(Iterable<? extends WorldSector> sectors) {
        worldSectorRepository.deleteAll(sectors);
    }
}
