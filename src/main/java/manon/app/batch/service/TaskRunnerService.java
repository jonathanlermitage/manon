package manon.app.batch.service;

import manon.app.batch.err.TaskNotFoundException;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

public interface TaskRunnerService {
    
    ExitStatus run(String task) throws TaskNotFoundException,
        JobParametersInvalidException, JobExecutionAlreadyRunningException,
        JobRestartException, JobInstanceAlreadyCompleteException;
}
