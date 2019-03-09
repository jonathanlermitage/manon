package manon.service.app;

public interface PerformanceRecorder {
    
    boolean isEmpty();
    
    /**
     * Log and return collected statistics since application start.
     * @return statistics as readable text.
     */
    String showStats();
}
