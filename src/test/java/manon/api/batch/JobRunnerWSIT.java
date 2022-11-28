package manon.api.batch;

import com.icegreen.greenmail.util.GreenMailUtil;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import manon.model.batch.TaskStatus;
import manon.util.basetest.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

class JobRunnerWSIT extends AbstractIT {

    private static final TaskStatus TASK_STATUS_COMPLETED = TaskStatus.builder()
        .running(ExitStatus.COMPLETED.isRunning())
        .exitCode(ExitStatus.COMPLETED.getExitCode())
        .exitDescription(ExitStatus.COMPLETED.getExitDescription())
        .build();

    @Test
    void shouldStartUserSnapshotTask() throws MessagingException {
        Response res = whenAdmin().getSpec()
            .post(API_SYS + "/batch/start/userSnapshotJob");
        res.then()
            .contentType(ContentType.JSON)
            .statusCode(SC_OK);

        greenMail.waitForIncomingEmail(8000 /* first email can be slow (GreenMail issue?) */, 1);
        assertThat(readValue(res, TaskStatus.class)).isEqualTo(TASK_STATUS_COMPLETED);
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(greenMail.getReceivedMessagesForDomain(cfg.getBatchNotificationEmailTo())).hasSize(1);
        assertThat(messages[0].getSubject())
            .isEqualTo("MANON executed batch 'userSnapshotJob', COMPLETED");
        assertThat(GreenMailUtil.getBody(messages[0]))
            .startsWith("MANON executed batch 'userSnapshotJob' that ended with status COMPLETED");
    }

    @Test
    void shouldNotStartUnknownTask() {
        whenAdmin().getSpec()
            .post(API_SYS + "/batch/start/foo")
            .then()
            .statusCode(SC_NOT_FOUND);

        greenMail.waitForIncomingEmail(1000, 1);
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }
}
