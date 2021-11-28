package manon.api.batch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.model.batch.TaskStatus;
import manon.model.user.UserSimpleDetails;
import manon.service.app.NotificationService;
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

/** Job API. */
@Tag(name = "Start batch jobs. Used by: admin.")
@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
@Slf4j
public class JobRunnerWS {

    private final JobRunnerService jobRunnerService;
    private final NotificationService notificationService;

    @Operation(summary = "Start given job.")
    @PostMapping(value = "/batch/start/{job}")
    public TaskStatus startJob(@AuthenticationPrincipal UserSimpleDetails sys, @PathVariable("job") String job)
        throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
        JobRestartException, JobInstanceAlreadyCompleteException {
        log.warn("admin {} starts job {}", sys.getUsername(), job);
        ExitStatus exitStatus = jobRunnerService.run(job);
        TaskStatus build = TaskStatus.builder()
            .running(exitStatus.isRunning())
            .exitCode(exitStatus.getExitCode())
            .exitDescription(exitStatus.getExitDescription())
            .build();
        notificationService.notifyBatchExecution(job, exitStatus.getExitCode());
        return build;
    }
}
