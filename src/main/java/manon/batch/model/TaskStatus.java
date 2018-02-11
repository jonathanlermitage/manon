package manon.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Builder(toBuilder = true)
public class TaskStatus {
    
    private boolean running;
    private String exitCode;
    private String exitDescription;
}
