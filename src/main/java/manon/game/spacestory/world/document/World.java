package manon.game.spacestory.world.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

@Document(collection = "SpaceStoryWorld")
@TypeAlias("SpaceStoryWorld")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate", "sectors"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class World implements Serializable, WorldSummary {
    
    @Id
    private String id;
    
    private String name;
    
    private int nbSectors;
    
    @DBRef
    private List<WorldSector> sectors;
    
    private int nbPoints;
    
    @DBRef
    private List<WorldPoint> points;
    
    @Version
    private long version;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @LastModifiedDate
    private Date updateDate;
    
    /** {@link World} fields validation rules. */
    public static class Validation {
        public static final int NAME_MIN_LENGTH = 3;
        public static final int NAMEID_MAX_LENGTH = 1024;
        public static final String NAME_SIZE_ERRMSG = "NAME_SIZE";
        
        public static final int SECTOR_MAX_WIDTH = 1_000_000;
        public static final String SECTOR_WIDTH_VAL_ERRMSG = "SECTOR_WIDTH_VAL";
        
        public static final int SECTOR_MAX_HEIGHT = 1_000_000;
        public static final String SECTOR_HEIGHT_VAL_ERRMSG = "SECTOR_HEIGHT_VAL";
        
        public static final int NB_SECTORS_HORIZONTAL_MAX_VAL = 1_000;
        public static final String NB_SECTORS_HORIZONTAL_VAL_ERRMSG = "NB_SECTORS_HORIZONTAL_VAL";
        
        public static final int NB_SECTORS_VERTICAL_MAX_VAL = 1_000;
        public static final String NB_SECTORS_VERTICAL_VAL_ERRMSG = "NB_SECTORS_VERTICAL_VAL";
        
        public static final int NB_POINTS_PER_SECTOR_MAX_VAL = 1_000_000;
        public static final String NB_POINTS_PER_SECTOR_VAL_ERRMSG = "NB_POINTS_PER_SECTOR_VAL";
        
        private Validation() {
            // utility class
        }
    }
}
