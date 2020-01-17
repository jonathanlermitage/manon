package manon.service.app;

public interface PerformanceRecorder {

    /**
     * Return collected statistics since application start.
     * @return statistics as readable text.
     */
    String getStats();
}
