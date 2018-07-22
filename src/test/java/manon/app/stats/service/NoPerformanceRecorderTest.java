package manon.app.stats.service;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NoPerformanceRecorderTest {
    
    @Test
    public void shouldCheckIsEmpty() {
        NoPerformanceRecorder performanceRecorder = new NoPerformanceRecorder();
        assertTrue(performanceRecorder.isEmpty());
    }
    
    @Test
    public void shouldCheckShowStats() {
        NoPerformanceRecorder performanceRecorder = new NoPerformanceRecorder();
        assertEquals(performanceRecorder.showStats(), "");
    }
}
