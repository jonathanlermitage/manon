package manon.app.config;

public interface API {
    
    String API_V1 = "/api/v1";
    
    /** Operations on users. */
    String API_USER = API_V1 + "/user";
    
    /** Administration operations on users.*/
    String API_USER_ADMIN = API_V1 + "/admin/user";
    
    /** Lobby trace. */
    String API_LOBBY = API_V1 + "/lobby";
    
    /** Operations on worlds. */
    String API_WORLD = API_V1 + "/world";
    
    /** Administration operations on worlds.*/
    String API_WORLD_ADMIN = API_V1 + "/admin/world";
    
    /** System trace. */
    String API_SYS = API_V1 + "/sys";
}
