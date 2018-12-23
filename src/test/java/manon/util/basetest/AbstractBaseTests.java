package manon.util.basetest;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import static manon.util.Tools.objId;

public abstract class AbstractBaseTests extends AbstractTestNGSpringContextTests {
    
    public static final String ADMIN_NAME = "ROOT"; // IMPORTANT must reflect application.yml:manon.admin.default-admin.username
    public static final String ADMIN_PWD = "woot"; // IMPORTANT must reflect application.yml:manon.admin.default-admin.password
    
    private static final String API_V1 = "/api/v1";
    public static final String API_USER = API_V1 + "/user";
    public static final String API_USER_ADMIN = API_V1 + "/admin/user";
    public static final String API_SYS = API_V1 + "/sys";
    
    public static final String ERRORS_MSG = "errors.defaultMessage";
    
    /** Simply a new unique objectId. */
    public static final String UNKNOWN_ID = objId();
    
    public static final String UNKNOWN_USER_NAME = "u" + UNKNOWN_ID;
    
    /** Number of times to start a batch to ensure its repeatability. */
    public static final int NB_TESTS_TO_ENSURE_BATCH_REPEATABILITY = 3;
    
    /** Number of batch chunks to process to ensure its reliability. */
    public static final int NB_BATCH_CHUNKS_TO_ENSURE_RELIABILITY = 3;
}
