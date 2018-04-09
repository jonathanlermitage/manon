package manon.app.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
@Builder(toBuilder = true)
public class TaskStatus {
    
    private boolean running;
    private String exitCode;
    private String exitDescription;
}
