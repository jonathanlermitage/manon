package manon.game.spacestory.world.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;
import static manon.game.spacestory.world.document.World.Validation.NAMEID_MAX_LENGTH;
import static manon.game.spacestory.world.document.World.Validation.NAME_MIN_LENGTH;
import static manon.game.spacestory.world.document.World.Validation.NAME_SIZE_ERRMSG;
import static manon.game.spacestory.world.document.World.Validation.NB_POINTS_PER_SECTOR_MAX_VAL;
import static manon.game.spacestory.world.document.World.Validation.NB_POINTS_PER_SECTOR_VAL_ERRMSG;
import static manon.game.spacestory.world.document.World.Validation.NB_SECTORS_HORIZONTAL_MAX_VAL;
import static manon.game.spacestory.world.document.World.Validation.NB_SECTORS_HORIZONTAL_VAL_ERRMSG;
import static manon.game.spacestory.world.document.World.Validation.NB_SECTORS_VERTICAL_MAX_VAL;
import static manon.game.spacestory.world.document.World.Validation.NB_SECTORS_VERTICAL_VAL_ERRMSG;
import static manon.game.spacestory.world.document.World.Validation.SECTOR_HEIGHT_VAL_ERRMSG;
import static manon.game.spacestory.world.document.World.Validation.SECTOR_MAX_HEIGHT;
import static manon.game.spacestory.world.document.World.Validation.SECTOR_MAX_WIDTH;
import static manon.game.spacestory.world.document.World.Validation.SECTOR_WIDTH_VAL_ERRMSG;

@Data
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class WorldRegistrationForm {
    
    @NotNull(message = NAME_SIZE_ERRMSG)
    @Size(min = NAME_MIN_LENGTH, max = NAMEID_MAX_LENGTH, message = NAME_SIZE_ERRMSG)
    private String name;
    
    @Positive(message = SECTOR_WIDTH_VAL_ERRMSG)
    @Max(value = SECTOR_MAX_WIDTH, message = SECTOR_WIDTH_VAL_ERRMSG)
    private long sectorWidth;
    
    @Positive(message = SECTOR_HEIGHT_VAL_ERRMSG)
    @Max(value = SECTOR_MAX_HEIGHT, message = SECTOR_HEIGHT_VAL_ERRMSG)
    private long sectorHeight;
    
    @Positive(message = NB_SECTORS_HORIZONTAL_VAL_ERRMSG)
    @Max(value = NB_SECTORS_HORIZONTAL_MAX_VAL, message = NB_SECTORS_HORIZONTAL_VAL_ERRMSG)
    private int nbSectorsHorizontal;
    
    @Positive(message = NB_SECTORS_VERTICAL_VAL_ERRMSG)
    @Max(value = NB_SECTORS_VERTICAL_MAX_VAL, message = NB_SECTORS_VERTICAL_VAL_ERRMSG)
    private int nbSectorsVertical;
    
    @Positive(message = NB_POINTS_PER_SECTOR_VAL_ERRMSG)
    @Max(value = NB_POINTS_PER_SECTOR_MAX_VAL, message = NB_POINTS_PER_SECTOR_VAL_ERRMSG)
    private int nbPointsPerSector;
}
