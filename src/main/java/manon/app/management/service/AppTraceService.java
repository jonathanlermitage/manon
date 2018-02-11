package manon.app.management.service;

import manon.app.management.document.AppTrace;
import manon.util.VisibleForTesting;

import java.util.List;

public interface AppTraceService {
    
    String getAppId();
    
    void deleteByCurrentAppIdAndEvent(AppTrace.Event event);
    
    /**
     * Log a message to database.
     * @param level log level.
     * @param event category.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void log(AppTrace.Level level, AppTrace.Event event, String msg, Object... args);
    
    void logUptime();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long count();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long countByCurrentAppId();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long countByCurrentAppIdAndEvent(AppTrace.Event event);
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    List<AppTrace> findAll();
}
