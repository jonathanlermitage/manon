package manon.snapshot.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.batch.core.BatchStatus.COMPLETED;

@Component
@Slf4j
public class UserSnapshotTaskListener extends JobExecutionListenerSupport {
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);
        String jobName = jobExecution.getJobInstance().getJobName();
        BatchStatus jobStatus = jobExecution.getStatus();
        if (jobExecution.getStatus() == COMPLETED) {
            log.info("job {} ended in state {}", jobName, jobStatus);
        } else {
            log.error("job {} ended in state {}", jobName, jobStatus);
        }
    }
}
