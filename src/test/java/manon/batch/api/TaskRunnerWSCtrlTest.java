package manon.batch.api;

import manon.util.basetest.MockBeforeClass;
import manon.util.basetest.Rs;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class TaskRunnerWSCtrlTest extends MockBeforeClass {
    
    @MockBean
    private TaskRunnerWS ws;
    
    @BeforeMethod
    private void clearInvocations() {
        Mockito.clearInvocations(ws);
    }
    
    @Test(dataProvider = DP_ALLOW_ADMIN)
    public void shouldVerifyStartTask(Rs rs, Integer status) throws Exception {
        rs.getRequestSpecification()
                .pathParam("task", "foobar")
                .post(API_TASK + "/start/{task}")
                .then()
                .statusCode(status);
        verify(ws, status).startTask(any(), eq("foobar"));
    }
}
