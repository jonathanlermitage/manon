package manon.app.config;

/** Spring profiles. */
public final class SpringProfiles {
    
    /** {@value}, an <b>additional profile</b> to enable performance metrics collection. */
    public static final String METRICS = "metrics";
    
    /** {@value}. */
    public static final String NOT_METRICS = "!metrics";
    
    private SpringProfiles() {
        // utility class
    }
}
