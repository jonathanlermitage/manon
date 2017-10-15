package manon.batch;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TaskStatus {
    
    private boolean running;
    private String exitCode;
    private String exitDescription;
}
