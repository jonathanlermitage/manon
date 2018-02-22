package manon.app.stats.service;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class NoPerformanceRecorderTest {
    
    @Test
    public void shouldCheckIsEmpty() {
        NoPerformanceRecorder performanceRecorder= new NoPerformanceRecorder();
        assertEquals(performanceRecorder.isEmpty(), true);
    }
    
    @Test
    public void shouldCheckShowStats() {
        NoPerformanceRecorder performanceRecorder= new NoPerformanceRecorder();
        assertEquals(performanceRecorder.showStats(), "");
    }
}
