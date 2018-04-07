package manon.app.trace.service;

import manon.app.trace.document.AppTrace;
import manon.app.trace.model.AppTraceEvent;
import manon.app.trace.model.AppTraceLevel;
import manon.util.VisibleForTesting;

import java.util.List;

public interface AppTraceService {
    
    String getAppId();
    
    void deleteByCurrentAppIdAndEvent(AppTraceEvent event);
    
    /**
     * Log a message to database.
     * @param level log level.
     * @param event category.
     * @param msg log message.
     */
    void log(AppTraceLevel level, AppTraceEvent event, String msg);
    
    void log(AppTraceLevel level, AppTraceEvent event);
    
    void logUptime();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long count();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long countByCurrentAppId();
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    long countByCurrentAppIdAndEvent(AppTraceEvent event);
    
    @VisibleForTesting(why = "AppTraceServiceTest")
    List<AppTrace> findAll();
}
