package manon.profile;

/**
 * Enable sharing options.
 */
public enum ProfileSharingOptionsEnum {
    
    /** Share gaming performance data (aka Skill): k/d ratio, victories/defeats ratio.
     * It can't be used to determine if a user spends too much time on this game. */
    SHARE_GAMING_PERFORMANCE,
    
    /** Share gaming data that's not related to performance: number of played games,
     * time spent on the game, etc. It could be used to determine if a user spends too
     * much time on this game. */
    SHARE_GAMING_DATA,
    
    /** Share friendship. Only existing friendship, not pending or rejected requests. */
    SHARE_FRIENDSHIP
}
