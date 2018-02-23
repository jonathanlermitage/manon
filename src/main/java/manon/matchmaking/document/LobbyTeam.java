package manon.matchmaking.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import manon.matchmaking.LobbyLeague;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

/**
 * A team of users that wait for matchmaking.
 */
@Document(collection = "LobbyTeam")
@TypeAlias("LobbyTeam")
@Getter
@ToString
@EqualsAndHashCode(exclude = {"creationDate", "updateDate"})
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class LobbyTeam {
    
    @Id
    private String id;
    
    @Builder.Default
    private List<String> userIds = new ArrayList<>();
    
    /** User that leads the team. */
    private String leader;
    
    private long skill;
    
    @Builder.Default
    private boolean ready = false;
    
    @Builder.Default
    private short maxSize = Default.MAX_TEAM_SIZE;
    
    private LobbyLeague league;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    @JsonFormat(pattern = DATE_FORMAT)
    @LastModifiedDate
    private Date updateDate;
    
    /**
     * {@link LobbyTeam} fields default or initial values.
     */
    public static class Default {
        public static final short MAX_TEAM_SIZE = 6;
    }
}
