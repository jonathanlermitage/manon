package manon.app.stats.service;

public interface PerformanceRecorder {
    
    boolean isEmpty();
    
    /**
     * Log and return collected statistics since application start.
     * @return statistics as readable text.
     */
    String showStats();
}
