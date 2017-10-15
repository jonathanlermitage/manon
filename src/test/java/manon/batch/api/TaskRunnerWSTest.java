package manon.batch.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import manon.batch.TaskStatus;
import manon.util.basetest.InitBeforeTest;
import manon.util.basetest.Rs;
import org.springframework.batch.core.ExitStatus;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertEquals;

public class TaskRunnerWSTest extends InitBeforeTest {
    
    private final TaskStatus completed = TaskStatus.builder()
            .running(ExitStatus.COMPLETED.isRunning())
            .exitCode(ExitStatus.COMPLETED.getExitCode())
            .exitDescription(ExitStatus.COMPLETED.getExitDescription())
            .build();
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldStartProfileSnapshotTask(Rs rs) {
        Response res = rs.getRequestSpecification()
                .post(getApiV1() + TEST_API_TASK + "/start/profileSnapshotJob");
        res.then()
                .contentType(ContentType.JSON)
                .statusCode(SC_OK);
        assertEquals(readValue(res, TaskStatus.class), completed);
    }
    
    @Test
    public void shouldNotProfileSnapshotTask_anonymous() {
        whenAnonymous().getRequestSpecification()
                .get(getApiV1() + TEST_API_TASK + "/start/profileSnapshotJob")
                .then()
                .statusCode(SC_UNAUTHORIZED);
    }
    
    @Test(dataProvider = DP_RS_USERS_NO_ADMIN)
    public void shouldNotProfileSnapshotTask_roleNonAdmin(Rs rs) {
        rs.getRequestSpecification()
                .get(getApiV1() + TEST_API_TASK + "/start/profileSnapshotJob")
                .then()
                .statusCode(SC_FORBIDDEN);
    }
    
    @Test(dataProvider = DP_RS_ADMINS)
    public void shouldNotStartUnknownTask(Rs rs) {
        rs.getRequestSpecification()
                .post(getApiV1() + TEST_API_TASK + "/start/foo")
                .then()
                .statusCode(SC_NOT_FOUND);
    }
}
