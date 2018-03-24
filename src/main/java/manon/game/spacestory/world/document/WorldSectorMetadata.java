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
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

@Document(collection = "SpaceStoryWorldSectorMetadata")
@TypeAlias("SpaceStoryWorldSectorMetadata")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"version", "creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class WorldSectorMetadata implements Serializable {
    
    @Id
    private String id;
    
    @Version
    private long version;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @LastModifiedDate
    private Date updateDate;
}
