package manon.app.config;

public final class API {
    
    public static final String API_V1 = "/api/v1";
    
    /** Operations on users. */
    public static final String API_USER = API_V1 + "/user";
    
    /** Administration operations on users.*/
    public static final String API_USER_ADMIN = API_V1 + "/admin/user";
    
    /** Lobby trace. */
    public static final String API_LOBBY = API_V1 + "/lobby";
    
    /** Operations on worlds. */
    public static final String API_WORLD = API_V1 + "/world";
    
    /** Administration operations on worlds.*/
    public static final String API_WORLD_ADMIN = API_V1 + "/admin/world";
    
    /** System trace. */
    public static final String API_SYS = API_V1 + "/sys";
    
    private API() {
        // utility class
    }
}
