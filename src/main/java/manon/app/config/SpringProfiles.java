package manon.app.config;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/** Spring profiles. */
@NoArgsConstructor(access = PRIVATE)
public final class SpringProfiles {
    
    /** {@value}, an <b>additional profile</b> to enable performance metrics collection. */
    public static final String METRICS = "metrics";
    
    /** {@value}. */
    public static final String NOT_METRICS = "!metrics";
}
