package manon.app.batch.service;

import lombok.RequiredArgsConstructor;
import manon.app.batch.err.TaskNotFoundException;
import manon.user.batch.UserSnapshotTask;
import manon.util.Tools;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskRunnerServiceImpl implements InitializingBean, TaskRunnerService {
    
    private final JobLauncher launcher;
    
    @Qualifier(UserSnapshotTask.JOB_NAME)
    private final Job userSnapshotJob;
    
    private Map<String, Job> jobs;
    
    @Override
    public void afterPropertiesSet() {
        this.jobs = Collections.singletonMap(UserSnapshotTask.JOB_NAME, userSnapshotJob);
    }
    
    @Override
    public ExitStatus run(String task) throws TaskNotFoundException,
        JobParametersInvalidException, JobExecutionAlreadyRunningException,
        JobRestartException, JobInstanceAlreadyCompleteException {
        if (jobs.containsKey(task)) {
            return launcher.run(jobs.get(task), todayDateJobParameters()).getExitStatus();
        }
        throw new TaskNotFoundException();
    }
    
    private JobParameters todayDateJobParameters() {
        return new JobParametersBuilder().addDate("START_DATE", Tools.now()).toJobParameters();
    }
}
