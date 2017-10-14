package manon.batch.service;

import org.springframework.batch.core.ExitStatus;

public interface TaskRunnerService {
    
    ExitStatus run(String task) throws Exception;
}
