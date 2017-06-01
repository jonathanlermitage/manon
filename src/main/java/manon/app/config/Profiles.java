package manon.app.config;

/** Spring profiles. */
public class Profiles {
    
    /** {@value}, for running local application. */
    public static final String DEV = "dev";
    /** {@value}, for running production application. */
    public static final String PROD = "prod";
    /** {@value}, for running production slave application (no jobs) .*/
    public static final String PROD_SLAVE = "prod_slave";
    
    /** {@value}, for unit testing on CI.*/
    public static final String CI = "ci";
    /** {@value}, for local unit testing. */
    public static final String TEST = "test";
    
    /** {@value}, an <b>additional profile</b> to enable performance metrics collection. */
    public static final String METRICS = "metrics";
}
