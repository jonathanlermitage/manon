package manon.api.batch;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import manon.model.batch.TaskStatus;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class JobRunnerWSIT extends AbstractIT {
    
    private static final TaskStatus TASK_STATUS_COMPLETED = TaskStatus.builder()
        .running(ExitStatus.COMPLETED.isRunning())
        .exitCode(ExitStatus.COMPLETED.getExitCode())
        .exitDescription(ExitStatus.COMPLETED.getExitDescription())
        .build();
    
    @Test
    public void shouldStartUserSnapshotTask() {
        Response res = whenAdmin().getSpec()
            .post(API_SYS + "/batch/start/userSnapshotJob");
        res.then()
            .contentType(ContentType.JSON)
            .statusCode(SC_OK);
        assertThat(readValue(res, TaskStatus.class)).isEqualTo(TASK_STATUS_COMPLETED);
    }
    
    @Test
    public void shouldNotStartUnknownTask() {
        whenAdmin().getSpec()
            .post(API_SYS + "/batch/start/foo")
            .then()
            .statusCode(SC_NOT_FOUND);
    }
}
