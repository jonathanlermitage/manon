package manon.service.batch.impl;

import manon.batch.UserSnapshotJobConfig;
import manon.err.batch.TaskNotFoundException;
import manon.service.batch.JobRunnerService;
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
public class JobRunnerServiceImpl implements InitializingBean, JobRunnerService {

    public JobRunnerServiceImpl(JobLauncher launcher,
                                @Qualifier(UserSnapshotJobConfig.JOB_NAME) Job userSnapshotJob) {
        this.launcher = launcher;
        this.userSnapshotJob = userSnapshotJob;
    }

    private final JobLauncher launcher;
    private final Job userSnapshotJob;

    private Map<String, Job> jobs;

    @Override
    public void afterPropertiesSet() {
        this.jobs = Collections.singletonMap(UserSnapshotJobConfig.JOB_NAME, userSnapshotJob);
    }

    @Override
    public ExitStatus run(String job)
        throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
        JobRestartException, JobInstanceAlreadyCompleteException {
        if (jobs.containsKey(job)) {
            return launcher.run(jobs.get(job), todayDateJobParameters()).getExitStatus();
        }
        throw new TaskNotFoundException();
    }

    private JobParameters todayDateJobParameters() {
        return new JobParametersBuilder().addDate("START_DATE", Tools.nowAsDate()).toJobParameters();
    }
}
