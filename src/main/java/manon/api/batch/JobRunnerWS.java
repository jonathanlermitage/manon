package manon.api.batch;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.model.batch.TaskStatus;
import manon.model.user.UserSimpleDetails;
import manon.service.batch.JobRunnerService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.Globals.API.API_SYS;
import static manon.util.Tools.Media.JSON;

/** Job API. */
@Api(description = "Start batch jobs. Used by: admin.")
@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
@Slf4j
public class JobRunnerWS {
    
    private final JobRunnerService jobRunnerService;
    
    @ApiOperation(value = "Start given job.", produces = JSON, response = TaskStatus.class)
    @PostMapping(value = "/batch/start/{job}")
    public TaskStatus startJob(@AuthenticationPrincipal UserSimpleDetails sys, @PathVariable("job") String job)
        throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
        JobRestartException, JobInstanceAlreadyCompleteException {
        log.warn("admin {} starts job {}", sys.getUsername(), job);
        ExitStatus exitStatus = jobRunnerService.run(job);
        return TaskStatus.builder()
            .running(exitStatus.isRunning())
            .exitCode(exitStatus.getExitCode())
            .exitDescription(exitStatus.getExitDescription())
            .build();
    }
}
