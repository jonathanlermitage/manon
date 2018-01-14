package manon.util.basetest;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import static manon.util.Tools.objId;

public abstract class BaseTests extends AbstractTestNGSpringContextTests {
    
    public static final String ADMIN_NAME = "ROOT"; // IMPORTANT must reflect application.yml:manon.admin.defaultAdmin.username
    public static final String ADMIN_PWD = "woot"; // IMPORTANT must reflect application.yml:manon.admin.defaultAdmin.password
    
    public static final String UNKNOWN_USER_ID = objId();
    
    private static final String API_V1 = "/api/v1";
    public static final String API_USER = API_V1 + "/user";
    public static final String API_USER_ADMIN = API_V1 + "/user/admin";
    public static final String API_LOBBY = API_V1 + "/lobby";
    public static final String API_TASK = API_V1 + "/sys/task";
}
