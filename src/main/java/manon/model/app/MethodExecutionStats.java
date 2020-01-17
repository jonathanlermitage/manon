package manon.model.app;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** Method execution statistics. */
@Data
@AllArgsConstructor
public class MethodExecutionStats {

    private String service;
    private long calls;
    private long minTime;
    private long maxTime;
    private long totalTime;
    private List<Long> times;
}
