package manon.app.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskListener extends JobExecutionListenerSupport {
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);
        log.info("job {} ended in state {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getStatus());
    }
}
