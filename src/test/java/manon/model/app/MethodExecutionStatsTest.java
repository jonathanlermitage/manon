package manon.model.app;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;

class MethodExecutionStatsTest {

    @Test
    void shouldVerifyToString() {
        Assertions.assertThat(new MethodExecutionStats("s", 0, 0, 0, 0, singletonList(0L)).toString())
            .contains("service", "calls", "minTime", "maxTime", "totalTime", "times");
    }

    @Test
    public void shouldVerifyEqualsContract() {
        EqualsVerifier.simple().forClass(MethodExecutionStats.class)
            .verify();
    }
}
