package manon.app.config;

public interface API {
    
    String API_V1 = "/api/v1";
    
    /** Operations on users. */
    String API_USER = API_V1 + "/user";
    
    /** Administration operations on users.*/
    String API_USER_ADMIN = API_V1 + "/user/admin";
    
    /** Lobby management. */
    String API_LOBBY = API_V1 + "/lobby";
    
    /** System management. */
    String API_SYS = API_V1 + "/sys";
    
    /** Operations on tasks (Spring Batch).*/
    String API_TASK = API_V1 + "/sys/task";
}
