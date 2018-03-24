package manon.game.spacestory.world.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.game.spacestory.world.model.Point;
import manon.game.spacestory.world.model.WorldPointDescription;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

@Document(collection = "SpaceStoryWorldPoint")
@TypeAlias("SpaceStoryWorldPoint")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class WorldPoint implements Serializable {
    
    @Id
    private String id;
    
    private String name;
    
    private Point point;
    
    private WorldPointDescription description;
    
    @JsonIgnore
    private WorldPointMetadata metadata;
    
    private String worldId;
    
    private String sectorId;
    
    @Version
    private long version;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @LastModifiedDate
    private Date updateDate;
}
