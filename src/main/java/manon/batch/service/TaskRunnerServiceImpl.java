package manon.batch.service;

import manon.batch.TaskNotFoundException;
import manon.snapshot.batch.UserSnapshotTask;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

@Component
public class TaskRunnerServiceImpl implements InitializingBean, TaskRunnerService {
    
    private final JobLauncher launcher;
    private final Job userSnapshotJob;
    private Map<String, Job> jobs;
    
    @Autowired
    public TaskRunnerServiceImpl(JobLauncher launcher, @Qualifier(UserSnapshotTask.JOB_NAME) Job userSnapshotJob) {
        this.launcher = launcher;
        this.userSnapshotJob = userSnapshotJob;
    }
    
    @Override
    public void afterPropertiesSet() {
        Map<String, Job> jobs = new HashMap<>();
        jobs.put(UserSnapshotTask.JOB_NAME, userSnapshotJob);
        this.jobs = unmodifiableMap(jobs);
    }
    
    @Override
    public ExitStatus run(String task) throws TaskNotFoundException,
            JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        if (jobs.containsKey(task)) {
            return launcher.run(jobs.get(task), todayDateJobParameters()).getExitStatus();
        }
        throw new TaskNotFoundException(task);
    }
    
    private JobParameters todayDateJobParameters() {
        return new JobParametersBuilder().addDate("START_DATE", new Date()).toJobParameters();
    }
}
