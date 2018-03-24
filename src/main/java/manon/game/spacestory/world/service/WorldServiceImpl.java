package manon.game.spacestory.world.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.game.spacestory.world.WorldExistsException;
import manon.game.spacestory.world.WorldNotFoundException;
import manon.game.spacestory.world.document.World;
import manon.game.spacestory.world.document.WorldPoint;
import manon.game.spacestory.world.document.WorldSector;
import manon.game.spacestory.world.document.WorldSummary;
import manon.game.spacestory.world.form.WorldRegistrationForm;
import manon.game.spacestory.world.model.Coverage;
import manon.game.spacestory.world.model.Point;
import manon.game.spacestory.world.model.WorldPointDescription;
import manon.game.spacestory.world.model.WorldPointType;
import manon.game.spacestory.world.repository.WorldRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;
import static manon.game.spacestory.model.Strings.POINT_NAME;
import static manon.game.spacestory.model.Strings.SECTOR_NAME;
import static manon.game.spacestory.model.Strings.key;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorldServiceImpl implements WorldService {
    
    private final WorldRepository worldRepository;
    private final WorldSectorService worldSectorService;
    private final WorldPointService worldPointService;
    
    private static WorldPointType[] worldPointTypes = WorldPointType.values();
    private static int nbWorldPointTypes = worldPointTypes.length;
    
    @Override
    public long count() {
        return worldRepository.count();
    }
    
    @Override
    public String register(WorldRegistrationForm form) throws WorldExistsException {
        World world = createEmptyWorld(form);
        world = addSectorsToWorld(world, form);
        world = addPointsToWorld(world, form);
        return world.getId();
    }
    
    private World createEmptyWorld(WorldRegistrationForm form) throws WorldExistsException {
        String name = form.getName();
        if (worldRepository.existsByName(name)) {
            throw new WorldExistsException();
        }
        
        log.info("creating empty world '{}' (step 1/3)", name);
        long stepTime = currentTimeMillis();
        World world = worldRepository.save(World.builder().name(name).build());
        log.info("created empty world '{}' in {} ms (step 1/3)", name, currentTimeMillis() - stepTime);
        
        return world;
    }
    
    private World addSectorsToWorld(World world, WorldRegistrationForm form) {
        String name = form.getName();
        List<WorldSector> sectors = new ArrayList<>();
        
        log.info("creating world '{}' sectors (step 2/3)", name);
        long stepTime = currentTimeMillis();
        for (int idxHorizontal = 0; idxHorizontal < form.getNbSectorsHorizontal(); idxHorizontal++) {
            for (int idxVertical = 0; idxVertical < form.getNbSectorsVertical(); idxVertical++) {
                Coverage coverage = Coverage.builder()
                        .bottomLeftX(idxHorizontal * form.getSectorWidth())
                        .bottomLeftY(idxVertical * form.getSectorHeight())
                        .topRightX(((idxHorizontal + 1L) * form.getSectorWidth()) - 1L)
                        .topRightY(((idxVertical + 1L) * form.getSectorHeight()) - 1L)
                        .build();
                WorldSector sector = WorldSector.builder()
                        .name(key(SECTOR_NAME, idxHorizontal, idxVertical))
                        .coverage(coverage)
                        .worldId(world.getId())
                        .build();
                sectors.add(sector);
            }
        }
        List<WorldSector> sectorsWithId = worldSectorService.save(sectors);
        world = worldRepository.save(world.toBuilder()
                .sectors(sectorsWithId)
                .nbSectors(sectorsWithId.size())
                .build());
        log.info("created world '{}' sectors in {} ms (step 2/3)", name, currentTimeMillis() - stepTime);
        
        return world;
    }
    
    private World addPointsToWorld(World world, WorldRegistrationForm form) {
        String name = form.getName();
        
        log.info("creating world '{}' points (step 3/3)", name);
        long stepTime = currentTimeMillis();
        List<WorldPoint> pointsWithId = new ArrayList<>();
        world.getSectors().parallelStream().forEach(sector -> pointsWithId.addAll(
                worldPointService.save(createPoints(
                        form.getNbPointsPerSector(), sector.getCoverage(), sector.getId(), sector.getWorldId()))
        ));
        world = worldRepository.save(world.toBuilder()
                .points(pointsWithId)
                .nbPoints(pointsWithId.size())
                .build());
        log.info("created world '{}' points in {} ms (step 3/3)", name, currentTimeMillis() - stepTime);
        
        return world;
    }
    
    private List<WorldPoint> createPoints(int nbPoints, Coverage coverage, String sectorId, String worldId) {
        Set<Point> points = Collections.synchronizedSet(new HashSet<>());
        IntStream.range(0, nbPoints).parallel().forEach(value -> points.add(Point.builder()
                .x(RandomUtils.nextLong(coverage.getBottomLeftX(), coverage.getTopRightX() + 1L))
                .y(RandomUtils.nextLong(coverage.getBottomLeftY(), coverage.getTopRightY() + 1L))
                .build()));
        return points.stream()
                .map(point -> WorldPoint.builder()
                        .name(key(POINT_NAME, point.getX(), point.getY()))
                        .description(randomPointDescription())
                        .point(point)
                        .worldId(worldId)
                        .sectorId(sectorId)
                        .build()
                ).collect(Collectors.toList());
    }
    
    private WorldPointDescription randomPointDescription() {
        WorldPointType randPointType = worldPointTypes[RandomUtils.nextInt(0, nbWorldPointTypes - 1)];
        return WorldPointDescription.builder().type(randPointType).build();
    }
    
    @Override
    public void delete(String id) throws WorldNotFoundException {
        World world = read(id);
        worldPointService.delete(world.getPoints());
        worldSectorService.delete(world.getSectors());
        worldRepository.deleteById(id);
    }
    
    @Override
    public World read(String id) throws WorldNotFoundException {
        return worldRepository.findById(id).orElseThrow(WorldNotFoundException::new);
    }
    
    @Override
    public WorldSummary readWorldSummary(String id) throws WorldNotFoundException {
        return worldRepository.findSummaryById(id).orElseThrow(WorldNotFoundException::new);
    }
    
    @Override
    public List<WorldSummary> findAllWorldSummaries() {
        return worldRepository.findAllWorldSummaries();
    }
}
