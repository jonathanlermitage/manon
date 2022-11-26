package manon.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("job {} ended in state {}",
            jobExecution.getJobInstance().getJobName(),
            jobExecution.getStatus());
    }
}
