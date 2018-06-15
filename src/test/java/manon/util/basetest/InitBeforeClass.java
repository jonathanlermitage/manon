package manon.util.basetest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.Application;
import manon.app.info.service.InfoService;
import manon.app.stats.service.PerformanceRecorder;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.service.RegistrationService;
import manon.user.service.UserService;
import manon.util.Tools;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static java.lang.System.currentTimeMillis;
import static manon.util.Tools.MDC_KEY_ENV;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Recreate data before test class.
 * To recreate data before every test method, see {@link InitBeforeTest}.
 */
@Slf4j
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class)
public abstract class InitBeforeClass extends BaseTests {
    
    @Setter
    private boolean initialized = false;
    
    @LocalServerPort
    private int port;
    
    @Autowired
    protected PerformanceRecorder performanceRecorder;
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected RegistrationService registrationService;
    @Autowired
    protected InfoService infoService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private final Map<Integer, String> userIdCache = new HashMap<>();
    
    public long userCount;
    
    public int getNumberOfUsers() {
        return 2;
    }
    
    @BeforeClass
    public void beforeClass() {
        initialized = false;
        RestAssured.config.encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        userIdCache.clear();
        if (!initialized) {
            initDb();
            initialized = true;
        }
    }
    
    public void clearDb() {
        for (String cn : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.dropCollection(cn);
        }
    }
    
    public void initDb() throws UserExistsException {
        long t1 = currentTimeMillis();
        clearDb();
        registrationService.ensureAdmin();
        for (int idx = 0; idx < getNumberOfUsers(); idx++) {
            registrationService.registerPlayer(makeName(idx), makePwd(idx));
        }
        userCount = userService.count().block();
        MDC.put(MDC_KEY_ENV, "testng");
        log.debug("initDb from class {} took {} ms", this.getClass().getSimpleName(), currentTimeMillis() - t1);
        MDC.clear();
    }
    
    private String makeName(int idx) {
        return "USERNAME" + idx;
    }
    
    private String makePwd(int idx) {
        return "p4ssw0rd" + idx;
    }
    
    @AfterClass
    public void afterClass() {
        if (!performanceRecorder.isEmpty()) {
            log.info(performanceRecorder.showStats());
        }
        for (String cn : mongoTemplate.getDb().listCollectionNames()) {
            mongoTemplate.dropCollection(cn);
        }
        setInitialized(false);
    }
    
    //
    // Helpers: get generated test users and authenticate with their credentials
    //
    
    public Rs whenAnonymous() {
        return new Rs(RestAssured.given()
                .header("X-Request-Id", "0x-1")
                .auth().none(), "", "");
    }
    
    public Rs whenAdmin() {
        return new Rs(RestAssured.given().auth().basic(ADMIN_NAME, ADMIN_PWD), ADMIN_NAME, ADMIN_PWD);
    }
    
    /** When player n°humanId, where humanId is an index starting at 1. */
    public Rs whenPX(int humanId) {
        int idx = humanId - 1;
        RequestSpecification rs = RestAssured.given()
                .header("X-Request-Id", "0x" + idx)
                .auth().basic(makeName(idx), makePwd(idx));
        return new Rs(rs, makeName(idx), makePwd(idx));
    }
    
    /** When player 1. */
    public Rs whenP1() {
        return whenPX(1);
    }
    
    /** When player 2. */
    public Rs whenP2() {
        return whenPX(2);
    }
    
    /** When player 3. */
    public Rs whenP3() {
        return whenPX(3);
    }
    
    @SuppressWarnings("SameParameterValue")
    public String pwd(int humanId) {
        return makePwd(humanId - 1);
    }
    
    @SuppressWarnings("SameParameterValue")
    public String name(int humanId) {
        return makeName(humanId - 1);
    }
    
    /** Get user id of player n°humanId, where humanId is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    @SneakyThrows(UserNotFoundException.class)
    public String userId(int humanId) {
        return findAndCacheUserIdByIdx(humanId - 1);
    }
    
    private String findAndCacheUserIdByIdx(int idx) throws UserNotFoundException {
        if (!userIdCache.containsKey(idx)) {
            userIdCache.put(idx, userService.readIdByUsername(makeName(idx)).getId());
        }
        return userIdCache.get(idx);
    }
    
    //
    // DataProviders
    //
    
    public final String DP_AUTHENTICATED = "dataProviderAuthenticated";
    
    @DataProvider
    public Object[][] dataProviderAuthenticated() {
        return new Object[][]{
                {whenAdmin()},
                {whenP1()}
        };
    }
    
    public final String DP_ADMIN = "dataProviderAdmin";
    
    @DataProvider
    public Object[][] dataProviderAdmin() {
        return new Object[][]{
                {whenAdmin()}
        };
    }
    
    //
    // Utils
    //
    
    /** Convert object to JSON. */
    @SneakyThrows(IOException.class)
    public <T> T readValue(Response content, Class<T> valueType) {
        return Tools.JSON.readValue(content.asString(), valueType);
    }
    
    /** Compute a long string. */
    public String verylongString(String base) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append(base);
        }
        return sb.toString();
    }
}
