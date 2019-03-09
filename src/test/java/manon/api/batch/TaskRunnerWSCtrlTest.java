package manon.api.batch;

import manon.util.basetest.AbstractMockTest;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class TaskRunnerWSCtrlTest extends AbstractMockTest {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyStartTask(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("task", "foobar")
            .post(API_SYS + "/batch/start/{task}")
            .then()
            .statusCode(status);
        verify(taskRunnerWS, status).startTask(any(), eq("foobar"));
    }
}
