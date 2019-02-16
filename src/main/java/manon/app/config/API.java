package manon.app.config;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class API {
    
    public static final String API_V1 = "/api/v1";
    
    /** Operations on users. */
    public static final String API_USER = API_V1 + "/user";
    
    /** Administration operations on users.*/
    public static final String API_USER_ADMIN = API_V1 + "/admin/user";
    
    /** System. */
    public static final String API_SYS = API_V1 + "/sys";
}
