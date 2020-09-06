package manon.model.batch;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStatusTest {

    @Test
    void shouldVerifyToString() {
        assertThat(TaskStatus.builder().build().toString()).contains(
            "running", "exitCode", "exitDescription");
    }

    @Test
    void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(TaskStatus.class)
            .verify();
    }
}
