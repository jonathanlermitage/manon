package manon.app.batch.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import manon.app.batch.model.TaskStatus;
import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskRunnerWSIntegrationTest extends AbstractIntegrationTest {
    
    private static final TaskStatus TASK_STATUS_COMPLETED = TaskStatus.builder()
        .running(ExitStatus.COMPLETED.isRunning())
        .exitCode(ExitStatus.COMPLETED.getExitCode())
        .exitDescription(ExitStatus.COMPLETED.getExitDescription())
        .build();
    
    @Test
    public void shouldStartUserSnapshotTask() {
        Response res = whenAdmin().getRequestSpecification()
            .post(API_SYS + "/batch/start/userSnapshotJob");
        res.then()
            .contentType(ContentType.JSON)
            .statusCode(SC_OK);
        assertThat(readValue(res, TaskStatus.class)).isEqualTo(TASK_STATUS_COMPLETED);
    }
    
    @Test
    public void shouldNotStartUnknownTask() {
        whenAdmin().getRequestSpecification()
            .post(API_SYS + "/batch/start/foo")
            .then()
            .statusCode(SC_NOT_FOUND);
    }
}
