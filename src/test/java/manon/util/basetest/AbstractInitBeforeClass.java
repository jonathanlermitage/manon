package manon.util.basetest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.Application;
import manon.app.config.Cfg;
import manon.app.info.service.InfoService;
import manon.app.stats.service.PerformanceRecorder;
import manon.app.trace.repository.AppTraceRepository;
import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.repository.FriendshipEventRepository;
import manon.user.repository.FriendshipRepository;
import manon.user.repository.FriendshipRequestRepository;
import manon.user.repository.UserRepository;
import manon.user.repository.UserSnapshotRepository;
import manon.user.repository.UserStatsRepository;
import manon.user.service.RegistrationService;
import manon.user.service.UserService;
import manon.util.Tools;
import manon.util.web.Rs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static java.lang.System.currentTimeMillis;
import static manon.util.Tools.MDC_KEY_ENV;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Recreate data before test class.
 * To recreate data before every test method, see {@link AbstractInitBeforeTest}.
 */
@Slf4j
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class)
public abstract class AbstractInitBeforeClass extends AbstractTest {
    
    @Setter
    private boolean initialized = false;
    
    @LocalServerPort
    private int port;
    
    @Autowired
    protected PerformanceRecorder performanceRecorder;
    
    @Autowired
    protected Cfg cfg;
    
    @Autowired
    protected UserService userService;
    @Autowired
    protected RegistrationService registrationService;
    @Autowired
    protected InfoService infoService;
    
    @Autowired
    protected AppTraceRepository appTraceRepository;
    @Autowired
    protected FriendshipEventRepository friendshipEventRepository;
    @Autowired
    protected FriendshipRepository friendshipRepository;
    @Autowired
    protected FriendshipRequestRepository friendshipRequestRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserSnapshotRepository userSnapshotRepository;
    @Autowired
    protected UserStatsRepository userStatsRepository;
    
    private final Map<Integer, Long> userIdCache = new HashMap<>();
    
    public int userCount;
    
    public int getNumberOfUsers() {
        return 2;
    }
    
    /** Clear data before test class. Do NOT override it in non-abstract test classes. */
    @BeforeAll
    public final void beforeClass() {
        initialized = false;
        RestAssured.config.encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    /** Clear data before test class. Do NOT override it in non-abstract test classes. */
    @BeforeEach
    public void beforeMethod() throws Exception {
        userIdCache.clear();
        if (!initialized) {
            initDb();
            initialized = true;
        }
        additionalBeforeMethod();
    }
    
    /** Override do add logic that occurs after the <code>@BeforeMethod</code> {@link #beforeMethod()}. */
    public void additionalBeforeMethod() {
        // override if needed
    }
    
    public void clearDb() {
        appTraceRepository.deleteAll();
        
        friendshipEventRepository.deleteAll();
        friendshipRequestRepository.deleteAll();
        friendshipRepository.deleteAll();
        
        userSnapshotRepository.deleteAll();
        userStatsRepository.deleteAll();
        
        userRepository.deleteAll();
    }
    
    public void initDb() throws UserExistsException, UserNotFoundException {
        long t1 = currentTimeMillis();
        MDC.put(MDC_KEY_ENV, "junit");
        clearDb();
        registrationService.ensureAdmin();
        for (int idx = 0; idx < getNumberOfUsers(); idx++) {
            registrationService.registerPlayer(makeName(idx), makePwd(idx));
        }
        userCount = (int) userService.count();
        additionalInitDb();
        log.debug("initDb from class {} took {} ms", this.getClass().getSimpleName(), currentTimeMillis() - t1);
        MDC.clear();
    }
    
    /** Override do add logic that occurs at the end of the {@link #initDb()}. */
    public void additionalInitDb() throws UserExistsException, UserNotFoundException {
        // override if needed
    }
    
    private String makeName(int idx) {
        return "USERNAME" + idx;
    }
    
    private String makePwd(int idx) {
        return "p4ssw0rd" + idx;
    }
    
    @AfterAll
    public final void afterClass() {
        if (!performanceRecorder.isEmpty()) {
            log.info(performanceRecorder.showStats());
        }
        clearDb();
        setInitialized(false);
    }
    
    //
    // Helpers: get generated test users and authenticate with their credentials
    //
    
    public final Rs whenAnonymous() {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "anonymous-" + currentTimeMillis())
            .auth().none(), "", "");
    }
    
    public final Rs whenAdmin() {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "admin-" + currentTimeMillis())
            .auth().basic(ADMIN_NAME, ADMIN_PWD), ADMIN_NAME, ADMIN_PWD);
    }
    
    /** When player n°humanId, where humanId is an index starting at 1. */
    public final Rs whenPX(int humanId) {
        int idx = humanId - 1;
        RequestSpecification rs = RestAssured.given()
            .header("X-Request-Id", "player-" + currentTimeMillis())
            .auth().basic(makeName(idx), makePwd(idx));
        return new Rs(rs, makeName(idx), makePwd(idx));
    }
    
    /** When player 1. */
    public final Rs whenP1() {
        return whenPX(1);
    }
    
    /** When player 2. */
    public final Rs whenP2() {
        return whenPX(2);
    }
    
    /** When player 3. */
    public final Rs whenP3() {
        return whenPX(3);
    }
    
    @SuppressWarnings("SameParameterValue")
    public final String pwd(int humanId) {
        return makePwd(humanId - 1);
    }
    
    @SuppressWarnings("SameParameterValue")
    public final String name(int humanId) {
        return makeName(humanId - 1);
    }
    
    @SneakyThrows(UserNotFoundException.class)
    public final User user(int humanId) {
        return userService.readByUsername(name(humanId));
    }
    
    /** Get user id of player n°humanId, where humanId is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    @SneakyThrows(UserNotFoundException.class)
    public final long userId(int humanId) {
        return findAndCacheUserIdByIdx(humanId - 1);
    }
    
    private long findAndCacheUserIdByIdx(int idx) throws UserNotFoundException {
        if (!userIdCache.containsKey(idx)) {
            userIdCache.put(idx, userService.readIdByUsername(makeName(idx)).getId());
        }
        return userIdCache.get(idx);
    }
    
    //
    // Utils
    //
    
    /** Convert object to JSON. */
    @SneakyThrows(IOException.class)
    public final <T> T readValue(Response content, Class<T> valueType) {
        return Tools.JSON.readValue(content.asString(), valueType);
    }
    
    /** Compute a long string. */
    public final String verylongString(String base) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append(base);
        }
        return sb.toString();
    }
}
