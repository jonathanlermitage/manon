package manon.batch.service;

import manon.dashboard.batch.ProfileSnapshotTask;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TaskRunnerServiceImpl implements InitializingBean, TaskRunnerService {
    
    private final JobLauncher launcher;
    private final Job profileSnapshotJob;
    private Map<String, Job> jobs;
    
    @Autowired
    public TaskRunnerServiceImpl(JobLauncher launcher, @Qualifier(ProfileSnapshotTask.JOB_NAME) Job profileSnapshotJob) {
        this.launcher = launcher;
        this.profileSnapshotJob = profileSnapshotJob;
    }
    
    @Override
    public void afterPropertiesSet()
            throws Exception {
        Map<String, Job> jobs = new HashMap<>();
        jobs.put(ProfileSnapshotTask.JOB_NAME, profileSnapshotJob);
        this.jobs = Collections.unmodifiableMap(jobs);
    }
    
    @Override
    public ExitStatus run(String task) throws Exception {
        if (jobs.containsKey(task)) {
            return launcher.run(jobs.get(task), todayDateJobParameters()).getExitStatus();
        }
        throw new InvalidParameterException(task);
    }
    
    private JobParameters todayDateJobParameters() {
        return new JobParametersBuilder().addDate("START_DATE", new Date()).toJobParameters();
    }
}
