package manon.app.config;

/** Spring profiles. */
public interface SpringProfiles {
    
    /** {@value}, an <b>additional profile</b> to enable performance metrics collection. */
    String METRICS = "metrics";
    
    /** {@value}. */
    String NOT_METRICS = "!metrics";
    
    /** {@value}. */
    String REDIS_CACHE = "!embeddedcache";
    
    /** {@value}. */
    String EMBEDDED_CACHE = "embeddedcache";
}
