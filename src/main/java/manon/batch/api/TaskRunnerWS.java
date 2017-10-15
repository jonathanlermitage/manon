package manon.batch.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
import manon.batch.TaskStatus;
import manon.batch.service.TaskRunnerService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static manon.app.config.API.API_TASK;
import static manon.app.config.API.API_V1;

/** Task API. */
@RestController
@RequestMapping(value = API_V1 + API_TASK)
@RequiredArgsConstructor
@Slf4j
public class TaskRunnerWS {
    
    private final TaskRunnerService taskRunnerService;
    
    @PostMapping(value = "/start/{task}")
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
