package manon.app.management.service;

import manon.app.management.document.AppTrace;

public interface TraceService {
    
    /**
     * Log a debug message to database.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void debug(String msg, Object... args);
    
    /**
     * Log an error message to database.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void error(String msg, Object... args);
    
    /**
     * Log an information message to database.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void info(String msg, Object... args);
    
    /**
     * Log a warning message to database.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void warn(String msg, Object... args);
    
    /**
     * Log a message to database.
     * @param level log level.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void log(AppTrace.TRACE_LEVEL level, String msg, Object... args);
    
    /**
     * Log a message to database.
     * @param level log level.
     * @param category category.
     * @param msg log message.
     * @param args parameters to replace in log message (via 'String.format').
     */
    void log(AppTrace.TRACE_LEVEL level, AppTrace.CAT category, String msg, Object... args);
}
