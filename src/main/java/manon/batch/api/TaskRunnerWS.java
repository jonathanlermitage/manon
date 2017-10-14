package manon.batch.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manon.app.security.UserSimpleDetails;
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
    
    /** Get all users. */
    @PostMapping(value = "/{task}")
    public ExitStatus startJob(@AuthenticationPrincipal UserSimpleDetails sys, @PathVariable("task") String task)
            throws Exception {
        log.info("system user {} starts job {}", sys.getUsername(), task);
        return taskRunnerService.run(task);
    }
}
