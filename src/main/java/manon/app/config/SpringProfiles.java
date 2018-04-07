package manon.app.config;

/** Spring profiles. */
public final class SpringProfiles {
    
    /** {@value}, an <b>additional profile</b> to enable performance metrics collection. */
    public static final String METRICS = "metrics";
    
    /** {@value}. */
    public static final String NOT_METRICS = "!metrics";
    
    /** {@value}. */
    public static final String REDIS_CACHE = "!embeddedcache";
    
    /** {@value}. */
    public static final String EMBEDDED_CACHE = "embeddedcache";
    
    private SpringProfiles() {
        // utility class
    }
}
