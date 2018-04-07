package manon.game.world.service;

import manon.game.world.document.World;
import manon.game.world.document.WorldSector;
import manon.game.world.err.WorldExistsException;
import manon.game.world.err.WorldNotFoundException;
import manon.game.world.form.WorldRegistrationForm;
import manon.util.basetest.InitBeforeTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
                        .name("WORLD_1")
                        .sectorWidth(100_000L)
                        .sectorHeight(100_000L)
                        .nbSectorsHorizontal(3)
                        .nbSectorsVertical(2)
                        .nbPointsPerSector(100)
                        .build()},
                {WorldRegistrationForm.builder()
                        .name("WORLD_1")
                        .sectorWidth(200L)
                        .sectorHeight(100L)
                        .nbSectorsHorizontal(1)
                        .nbSectorsVertical(1)
                        .nbPointsPerSector(1000)
                        .build()},
                {WorldRegistrationForm.builder()
                        .name("WORLD_1")
                        .sectorWidth(1L)
                        .sectorHeight(1L)
                        .nbSectorsHorizontal(1)
                        .nbSectorsVertical(1)
                        .nbPointsPerSector(1)
                        .build()}
        };
    }
    
    @Test(dataProvider = "dataProviderShouldRegisterAndReadWorld")
    public void shouldRegisterAndReadWorld(WorldRegistrationForm form)
            throws WorldExistsException {
        World world = worldService.register(form);
        
        assertEquals(worldService.count(), 1);
        assertEquals(worldSectorService.count(), form.getNbSectorsHorizontal() * form.getNbSectorsVertical());
        assertThat(world.getSectors()).hasSize(form.getNbSectorsHorizontal() * form.getNbSectorsVertical());
        world.getSectors().forEach(sector -> assertEquals(sector.getWorldId(), world.getId()));
        
        long expectedNbPointsMax = (long) form.getNbPointsPerSector() * form.getNbSectorsHorizontal() * form.getNbSectorsVertical();
        assertThat(worldPointService.count()).isBetween((long) (0.8 * expectedNbPointsMax), expectedNbPointsMax);
        assertEquals(world.getPoints().size(), worldPointService.count());
        List<String> sectorIds = world.getSectors().stream().map(WorldSector::getId).collect(Collectors.toList());
        world.getPoints().forEach(point -> {
            assertEquals(point.getWorldId(), world.getId());
            assertThat(sectorIds).contains(point.getSectorId());
        });
    }
    
    @Test(expectedExceptions = WorldExistsException.class)
    public void shouldNotRegisterWorldTwice() throws WorldExistsException {
        for (int i = 0; i < 2; i++) {
            worldService.register(WorldRegistrationForm.builder()
                    .name("WORLD_1")
                    .sectorWidth(1000L)
                    .sectorHeight(1000L)
                    .nbSectorsHorizontal(1)
                    .nbSectorsVertical(1)
                    .nbPointsPerSector(1)
                    .build());
        }
    }
    
    @Test
    public void shouldRegisterManyWorlds() throws WorldExistsException {
        for (int i = 0; i < 2; i++) {
            worldService.register(WorldRegistrationForm.builder()
                    .name("WORLD_" + i)
                    .sectorWidth(1000L)
                    .sectorHeight(1000L)
                    .nbSectorsHorizontal(1)
                    .nbSectorsVertical(1)
                    .nbPointsPerSector(1)
                    .build());
        }
        
        assertEquals(worldService.count(), 2L);
        assertEquals(worldSectorService.count(), 2L);
        assertEquals(worldPointService.count(), 2L);
    }
    
    @Test(expectedExceptions = WorldNotFoundException.class)
    public void shouldNotReadUnknownWorld() throws WorldNotFoundException {
        worldService.read(UNKNOWN_ID);
    }
    
    @Test
    public void shouldDeleteWorld() throws WorldNotFoundException, WorldExistsException {
        String worldId = worldService.register(WorldRegistrationForm.builder()
                .name("WORLD_1")
                .sectorWidth(1000L)
                .sectorHeight(1000L)
                .nbSectorsHorizontal(1)
                .nbSectorsVertical(1)
                .nbPointsPerSector(1)
                .build()).getId();
        String otherWorldId = worldService.register(WorldRegistrationForm.builder()
                .name("WORLD_2")
                .sectorWidth(1000L)
                .sectorHeight(1000L)
                .nbSectorsHorizontal(1)
                .nbSectorsVertical(1)
                .nbPointsPerSector(1)
                .build()).getId();
        worldService.delete(worldId);
        
        assertEquals(worldService.count(), 1L);
        assertEquals(worldSectorService.count(), 1L);
        assertEquals(worldPointService.count(), 1L);
        assertNotNull(worldService.read(otherWorldId));
    }
    
    @Test(expectedExceptions = WorldNotFoundException.class)
    public void shouldNotDeleteUnknownWorld() throws WorldNotFoundException {
        worldService.delete(UNKNOWN_ID);
    }
}
