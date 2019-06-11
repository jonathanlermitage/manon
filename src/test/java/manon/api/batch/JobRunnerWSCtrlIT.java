package manon.api.batch;

import manon.util.basetest.AbstractMockIT;
import manon.util.web.Rs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class JobRunnerWSCtrlIT extends AbstractMockIT {
    
    @ParameterizedTest
    @MethodSource(DP_ALLOW_ADMIN)
    public void shouldVerifyStartTask(Rs rs, Integer status) throws Exception {
        rs.getSpec()
            .pathParam("job", "foobar")
            .post(API_SYS + "/batch/start/{job}")
            .then()
            .statusCode(status);
        verify(jobRunnerWS, status).startJob(any(), eq("foobar"));
    }
}
