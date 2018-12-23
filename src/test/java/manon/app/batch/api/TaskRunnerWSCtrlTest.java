package manon.app.batch.api;

import manon.util.basetest.AbstractMockBeforeClass;
import manon.util.basetest.Rs;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class TaskRunnerWSCtrlTest extends AbstractMockBeforeClass {
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyStartTask(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
            .pathParam("task", "foobar")
            .post(API_SYS + "/batch/start/{task}")
            .then()
            .statusCode(status);
        verify(taskRunnerWS, status).startTask(any(), eq("foobar"));
    }
}
