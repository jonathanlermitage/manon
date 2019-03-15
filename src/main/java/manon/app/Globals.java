package manon.app;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/** Static globals. */
@NoArgsConstructor(access = PRIVATE)
public class Globals {
    
    /** API paths. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class API {
        
        public static final String API_V1 = "/api/v1";
        
        /** Operations on users. */
        public static final String API_USER = API_V1 + "/user";
        
        /** Administration operations on users.*/
        public static final String API_USER_ADMIN = API_V1 + "/admin/user";
        
        /** System. */
        public static final String API_SYS = API_V1 + "/sys";
    }
    
    /** Properties. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Properties {
        
        /** {@value}, enable API performance recorder. Should be used in development or test environment only.. */
        public static final String ENABLE_PERFORMANCE_RECORDER = "manon.enable-performance-recorder";
    }
}
