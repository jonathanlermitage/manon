package manon.batch.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import manon.batch.model.TaskStatus;
import manon.util.basetest.InitBeforeTest;
import org.springframework.batch.core.ExitStatus;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.testng.Assert.assertEquals;

public class TaskRunnerWSTest extends InitBeforeTest {
    
    private static final TaskStatus TASK_STATUS_COMPLETED = TaskStatus.builder()
            .running(ExitStatus.COMPLETED.isRunning())
            .exitCode(ExitStatus.COMPLETED.getExitCode())
            .exitDescription(ExitStatus.COMPLETED.getExitDescription())
            .build();
    
    @Test
    public void shouldStartUserSnapshotTask() {
        Response res = whenAdmin().getRequestSpecification()
                .post(API_TASK + "/start/userSnapshotJob");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        assertEquals(readValue(res, TaskStatus.class), TASK_STATUS_COMPLETED);
    }
    
    @Test
    public void shouldNotStartUnknownTask() {
        whenAdmin().getRequestSpecification()
                .post(API_TASK + "/start/foo")
                .then()
                .statusCode(SC_NOT_FOUND);
    }
}
