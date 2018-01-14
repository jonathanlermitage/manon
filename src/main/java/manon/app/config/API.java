package manon.app.config;

public class API {
    
    private static final String API_V1 = "/api/v1";
    
    /** Operations on users. */
    public static final String API_USER = API_V1 + "/user";
    
    /** Administration operations on users.*/
    public static final String API_USER_ADMIN = API_V1 + "/user/admin";
    
    /** Lobby management. */
    public static final String API_LOBBY = API_V1 + "/lobby";
    
    /** System management. */
    public static final String API_SYS = API_V1 + "/sys";
    
    /** Operations on tasks (Spring Batch).*/
    public static final String API_TASK = API_V1 + "/sys/task";
}
