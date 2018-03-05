package manon.app.batch.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.batch.model.TaskStatus;
import manon.app.batch.service.TaskRunnerService;
import manon.app.security.UserSimpleDetails;
import org.springframework.batch.core.ExitStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_SYS;

/** Task API. */
@RestController
@RequestMapping(value = API_SYS)
@RequiredArgsConstructor
@Slf4j
public class TaskRunnerWS {
    
    private final TaskRunnerService taskRunnerService;
    
    @PostMapping(value = "/batch/start/{task}")
    public TaskStatus startTask(@AuthenticationPrincipal UserSimpleDetails sys, @PathVariable("task") String task)
            throws Exception {
        log.warn("admin {} starts task {}", sys.getUsername(), task);
        ExitStatus exitStatus = taskRunnerService.run(task);
        return TaskStatus.builder()
                .running(exitStatus.isRunning())
                .exitCode(exitStatus.getExitCode())
                .exitDescription(exitStatus.getExitDescription())
                .build();
    }
}
