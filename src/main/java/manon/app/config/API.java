package manon.app.config;

public class API {
    
    public static final String API_V1 = "/api/v1";
    
    /** Operations on users.
     * User registration.*/
    public static final String API_USER = "/user";
    
    /** Operations on profiles.
     * Friendship, profile data. */
    public static final String API_PROFILE = "/profile";
    
    /** Lobby management. */
    public static final String API_LOBBY = "/lobby";
    
    /** System management. */
    public static final String API_SYS = "/sys";
    
    /** Operations on tasks (Spring Batch).*/
    public static final String API_TASK = "/sys/task";
}
