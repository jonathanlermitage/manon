package manon.snapshot.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

@Document(collection = "ProfilesStats")
@TypeAlias("ProfilesStats")
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class ProfilesStats {
    
    @Id
    private String id;
    
    private int nbProfiles;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
}
