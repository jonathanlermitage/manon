package manon.game.world.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;
import static manon.game.world.document.World.Validation.NAME_MAX_LENGTH;
import static manon.game.world.document.World.Validation.NAME_MIN_LENGTH;
import static manon.game.world.document.World.Validation.NAME_SIZE_ERRMSG;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_POINTS_PER_SECTOR_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_HORIZONTAL_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_MAX_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_MIN_VAL;
import static manon.game.world.document.World.Validation.NB_SECTORS_VERTICAL_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.SECTOR_HEIGHT_VAL_ERRMSG;
import static manon.game.world.document.World.Validation.SECTOR_MAX_HEIGHT;
import static manon.game.world.document.World.Validation.SECTOR_MAX_WIDTH;
import static manon.game.world.document.World.Validation.SECTOR_MIN_HEIGHT;
import static manon.game.world.document.World.Validation.SECTOR_MIN_WIDTH;
import static manon.game.world.document.World.Validation.SECTOR_WIDTH_VAL_ERRMSG;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class WorldRegistrationForm {
    
    @NotNull(message = NAME_SIZE_ERRMSG)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_SIZE_ERRMSG)
    private String name;
    
    @Min(value = SECTOR_MIN_WIDTH, message = SECTOR_WIDTH_VAL_ERRMSG)
    @Max(value = SECTOR_MAX_WIDTH, message = SECTOR_WIDTH_VAL_ERRMSG)
    private long sectorWidth;
    
    @Min(value = SECTOR_MIN_HEIGHT, message = SECTOR_HEIGHT_VAL_ERRMSG)
    @Max(value = SECTOR_MAX_HEIGHT, message = SECTOR_HEIGHT_VAL_ERRMSG)
    private long sectorHeight;
    
    @Min(value = NB_SECTORS_HORIZONTAL_MIN_VAL, message = NB_SECTORS_HORIZONTAL_VAL_ERRMSG)
    @Max(value = NB_SECTORS_HORIZONTAL_MAX_VAL, message = NB_SECTORS_HORIZONTAL_VAL_ERRMSG)
    private int nbSectorsHorizontal;
    
    @Min(value = NB_SECTORS_VERTICAL_MIN_VAL, message = NB_SECTORS_VERTICAL_VAL_ERRMSG)
    @Max(value = NB_SECTORS_VERTICAL_MAX_VAL, message = NB_SECTORS_VERTICAL_VAL_ERRMSG)
    private int nbSectorsVertical;
    
    @Min(value = NB_POINTS_PER_SECTOR_MIN_VAL, message = NB_POINTS_PER_SECTOR_VAL_ERRMSG)
    @Max(value = NB_POINTS_PER_SECTOR_MAX_VAL, message = NB_POINTS_PER_SECTOR_VAL_ERRMSG)
    private int nbPointsPerSector;
}
