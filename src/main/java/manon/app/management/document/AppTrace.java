package manon.app.management.document;

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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static lombok.AccessLevel.PRIVATE;
import static manon.util.Tools.DATE_FORMAT;

@Document(collection = "AppTrace")
@TypeAlias("AppTrace")
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public final class AppTrace {
    
    @Id
    private String id;
    
    private String msg;
    
    @Indexed(background = true)
    private LVL level;
    
    @Indexed(background = true)
    private CAT cat;
    
    @Indexed(background = true)
    @JsonFormat(pattern = DATE_FORMAT)
    @CreatedDate
    private Date creationDate;
    
    public enum LVL {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
    
    public enum CAT {
        APP_START,
        APP_STOP,
        PERFORMANCE_STATS
    }
}
