package manon.app.stats.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static manon.app.config.SpringProfiles.NOT_METRICS;

@Service
@Profile(NOT_METRICS)
public class NoPerformanceRecorder implements PerformanceRecorder {
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public String showStats() {
        return "";
    }
}
