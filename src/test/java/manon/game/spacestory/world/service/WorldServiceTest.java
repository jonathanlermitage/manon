package manon.game.spacestory.world.service;

import manon.game.spacestory.world.WorldExistsException;
import manon.game.spacestory.world.WorldNotFoundException;
import manon.game.spacestory.world.document.World;
import manon.game.spacestory.world.document.WorldSector;
import manon.game.spacestory.world.form.WorldRegistrationForm;
import manon.util.basetest.InitBeforeTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static manon.game.spacestory.model.Strings.WORLD_NAME;
import static manon.game.spacestory.model.Strings.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class WorldServiceTest extends InitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Autowired
    protected WorldService worldService;
    @Autowired
    protected WorldSectorService worldSectorService;
    @Autowired
    protected WorldPointService worldPointService;
    
    @DataProvider
    public Object[][] dataProviderShouldRegisterAndReadWorld() {
        return new Object[][]{
                {WorldRegistrationForm.builder()
                        .name(key(WORLD_NAME, 0))
                        .sectorWidth(100_000L)
                        .sectorHeight(100_000L)
                        .nbSectorsHorizontal(3)
                        .nbSectorsVertical(2)
                        .nbPointsPerSector(100)
                        .build()},
                {WorldRegistrationForm.builder()
                        .name(key(WORLD_NAME, 0))
                        .sectorWidth(200L)
                        .sectorHeight(100L)
                        .nbSectorsHorizontal(1)
                        .nbSectorsVertical(1)
                        .nbPointsPerSector(1000)
                        .build()}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldRegisterAndReadWorld")
    public void shouldRegisterAndReadWorld(WorldRegistrationForm form)
            throws WorldExistsException, WorldNotFoundException {
        String worldId = worldService.register(form);
        World world = worldService.read(worldId);
        
        assertEquals(worldService.count(), 1);
        
        assertEquals(worldSectorService.count(), form.getNbSectorsHorizontal() * form.getNbSectorsVertical());
        assertThat(world.getSectors()).hasSize(form.getNbSectorsHorizontal() * form.getNbSectorsVertical());
        world.getSectors().forEach(sector -> assertEquals(sector.getWorldId(), worldId));
        
        long expectedNbPointsMax = (long) form.getNbPointsPerSector() * form.getNbSectorsHorizontal() * form.getNbSectorsVertical();
        assertThat(worldPointService.count()).isBetween((long) (0.8 * expectedNbPointsMax), expectedNbPointsMax);
        assertEquals(world.getPoints().size(), worldPointService.count());
        List<String> sectorIds = world.getSectors().stream().map(WorldSector::getId).collect(Collectors.toList());
        world.getPoints().forEach(point -> {
            assertEquals(point.getWorldId(), worldId);
            assertThat(sectorIds).contains(point.getSectorId());
        });
    }
    
    @Test(expectedExceptions = WorldExistsException.class)
    public void shouldNotRegisterWorldTwice() throws WorldExistsException {
        for (int i = 0; i < 2; i++) {
            worldService.register(WorldRegistrationForm.builder()
                    .name(key(WORLD_NAME, 0))
                    .sectorWidth(1000L)
                    .sectorHeight(1000L)
                    .nbSectorsHorizontal(1)
                    .nbSectorsVertical(1)
                    .nbPointsPerSector(1)
                    .build());
        }
    }
    
    @Test(expectedExceptions = WorldNotFoundException.class)
    public void shouldNotReadUnknownWorld() throws WorldNotFoundException {
        worldService.read(UNKNOWN_ID);
    }
    
    @Test
    public void shouldDeleteWorld() throws WorldNotFoundException, WorldExistsException {
        String worldId = worldService.register(WorldRegistrationForm.builder()
                .name(key(WORLD_NAME, 0))
                .sectorWidth(1000L)
                .sectorHeight(1000L)
                .nbSectorsHorizontal(1)
                .nbSectorsVertical(1)
                .nbPointsPerSector(1)
                .build());
        worldService.delete(worldId);
        
        assertEquals(worldService.count(), 0L);
        assertEquals(worldSectorService.count(), 0L);
        assertEquals(worldPointService.count(), 0L);
    }
    
    @Test(expectedExceptions = WorldNotFoundException.class)
    public void shouldNotDeleteUnknownWorld() throws WorldNotFoundException {
        worldService.delete(UNKNOWN_ID);
    }
}
