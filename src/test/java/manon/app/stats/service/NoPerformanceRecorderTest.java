package manon.app.stats.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoPerformanceRecorderTest {
    
    @Test
    public void shouldCheckIsEmpty() {
        NoPerformanceRecorder performanceRecorder = new NoPerformanceRecorder();
        assertThat(performanceRecorder.isEmpty()).isTrue();
    }
    
    @Test
    public void shouldCheckShowStats() {
        NoPerformanceRecorder performanceRecorder = new NoPerformanceRecorder();
        assertThat(performanceRecorder.showStats()).isEmpty();
    }
}
