package manon.app.management.service;

import manon.app.management.document.AppTrace;

public interface TraceService {
    
    /**
     * Log a message to database.
     * @param level log level.
     * @param category category.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void log(AppTrace.LVL level, AppTrace.CAT category, String msg, Object... args);
}
