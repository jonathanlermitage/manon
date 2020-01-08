package manon.util.web;

/**
 * How to retrieve authentication token.
 */
public enum AuthMode {

    /**
     * Get authentication token by calling regular authentication API.
     */
    REGULAR_VIA_API,
    /**
     * Get authentication token by calling internal token service, even if user
     * is in a state that would not allow authentication. Useful to call an API
     * with a user which has been recently banned, deleted or suspended.
     */
    FORCED_VIA_SERVICE
}
