package manon.util.basetest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.Application;
import manon.app.Cfg;
import manon.document.user.User;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.service.app.AppTraceService;
import manon.service.app.PerformanceRecorder;
import manon.service.app.PingService;
import manon.service.batch.JobRunnerService;
import manon.service.user.FriendshipEventService;
import manon.service.user.FriendshipRequestService;
import manon.service.user.FriendshipService;
import manon.service.user.PasswordEncoderService;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import manon.service.user.UserSnapshotService;
import manon.service.user.UserStatsService;
import manon.util.Tools;
import manon.util.web.Page;
import manon.util.web.Rs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static java.lang.System.currentTimeMillis;
import static manon.util.Tools.Mdc.KEY_ENV;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Base for integration tests.
 * Application starts with some users and one admin. Data is recreated before test methods.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(value = ExecutionMode.SAME_THREAD)
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, MockitoTestExecutionListener.class})
public abstract class AbstractIT {
    
    @LocalServerPort
    private int port;
    
    @Autowired(required = false)
    protected PerformanceRecorder performanceRecorder;
    
    @Autowired
    protected Cfg cfg;
    
    @SpyBean
    protected AppTraceService appTraceService;
    @SpyBean
    protected FriendshipService friendshipService;
    @SpyBean
    protected FriendshipEventService friendshipEventService;
    @SpyBean
    protected FriendshipRequestService friendshipRequestService;
    @SpyBean
    protected JobRunnerService jobRunnerService;
    @SpyBean
    protected PasswordEncoderService passwordEncoderService;
    @SpyBean
    protected PingService pingService;
    @SpyBean
    protected RegistrationService registrationService;
    @SpyBean
    protected UserDetailsService userDetailsService;
    @SpyBean
    protected UserService userService;
    @SpyBean
    protected UserSnapshotService userSnapshotService;
    @SpyBean
    protected UserStatsService userStatsService;
    
    private final String API_V1 = "/api/v1";
    public final String API_USER = API_V1 + "/user";
    public final String API_USER_ADMIN = API_V1 + "/admin/user";
    public final String API_SYS = API_V1 + "/sys";
    
    public static final String MANAGED_ERROR_TYPE = "errorType";
    public static final String VALIDATION_ERRORS_MSG = "errors.defaultMessage";
    
    /** Simply a new unique objectId. */
    public final long UNKNOWN_ID = Long.MAX_VALUE;
    
    public final String UNKNOWN_USER_NAME = "u" + UNKNOWN_ID;
    
    /** Number of times to start a batch to ensure its repeatability. */
    public final int NB_TESTS_TO_ENSURE_BATCH_REPEATABILITY = 3;
    
    /** Number of batch chunks to process to ensure its reliability. */
    public final int NB_BATCH_CHUNKS_TO_ENSURE_RELIABILITY = 3;
    
    protected final Map<Integer, Long> userIdCache = new HashMap<>();
    
    public int userCount;
    
    public int getNumberOfUsers() {
        return 2;
    }
    
    /** Clear data before test class. Do NOT override it in non-abstract test classes. */
    @BeforeAll
    public final void beforeClass() {
        RestAssured.config.encoderConfig(encoderConfig().defaultContentCharset(StandardCharsets.UTF_8));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    /** Clear data before each test method. */
    @BeforeEach
    public void clearData() throws Exception {
        userIdCache.clear();
        initDb();
        
        Mockito.clearInvocations(pingService);
    }
    
    private void clearDb() {
        appTraceService.deleteAll();
        
        friendshipEventService.deleteAll();
        friendshipRequestService.deleteAll();
        friendshipService.deleteAll();
        
        userSnapshotService.deleteAll();
        userStatsService.deleteAll();
        
        userService.deleteAll();
    }
    
    public void initDb() throws ExecutionException, InterruptedException {
        long t1 = currentTimeMillis();
        MDC.put(KEY_ENV, "junit");
        clearDb();
        
        Executor tasksExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> registerPlayerTask = CompletableFuture.runAsync(() -> {
            for (int idx = 0; idx < getNumberOfUsers(); idx++) {
                try {
                    registrationService.registerPlayer(makeName(idx), makePwd(idx));
                } catch (UserExistsException e) {
                    throw new IllegalStateException(e);
                }
            }
        }, tasksExecutor);
        CompletableFuture<Void> ensureUsersTask = CompletableFuture.runAsync(() -> {
            registrationService.ensureActuator();
            registrationService.ensureAdmin();
            registrationService.ensureDev();
        }, tasksExecutor);
        CompletableFuture<Void> additionalInitDbTask = CompletableFuture.runAsync(() -> {
            try {
                additionalParallelInitDb();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }, tasksExecutor);
        CompletableFuture.allOf(registerPlayerTask, ensureUsersTask, additionalInitDbTask).get();
        
        userCount = (int) userService.count();
        
        log.debug("initDb from class {} took {} ms", this.getClass().getSimpleName(), currentTimeMillis() - t1);
        MDC.clear();
    }
    
    /** Override do add logic that occurs at the end of the {@link #initDb()}. */
    public void additionalParallelInitDb() throws Exception {
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
        MDC.put(KEY_ENV, "junit");
        if (performanceRecorder != null && !performanceRecorder.getStats().isEmpty()) {
            log.info(performanceRecorder.getStats());
        }
        logGCStats();
        MDC.clear();
    }
    
    private void logGCStats() {
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            totalGarbageCollections += gc.getCollectionCount();
            garbageCollectionTime += gc.getCollectionTime();
        }
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long gcActivityRatio = (garbageCollectionTime * 100) / uptime;
        log.info("GC stats:\n - total GC collections: {}\n - total GC collection time: {}ms (activity ratio: {}%)\n - JVM uptime: {}ms\n",
            totalGarbageCollections, garbageCollectionTime, gcActivityRatio, uptime);
        assertThat(gcActivityRatio).as("GC pressure should be low (less than 10% execution time)").isLessThan(10);
    }
    
    //
    // Helpers: get generated test users and authenticate with their credentials
    //
    
    public final Rs whenAnonymous() {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "anon-" + currentTimeMillis())
            .auth().none(), "", "");
    }
    
    public final Rs whenAuthenticated(String username, String password) {
        return new Rs(RestAssured.given()
            .header("X-Request-Id", "user-" + currentTimeMillis())
            .auth().preemptive().basic(username, password), username, password);
    }
    
    public final Rs whenActuator() {
        return whenAuthenticated(cfg.getDefaultUserActuatorUsername(), cfg.getDefaultUserActuatorPassword());
    }
    
    public final Rs whenAdmin() {
        return whenAuthenticated(cfg.getDefaultUserAdminUsername(), cfg.getDefaultUserAdminPassword());
    }
    
    public final Rs whenDev() {
        return whenAuthenticated(cfg.getDefaultUserDevUsername(), cfg.getDefaultUserDevPassword());
    }
    
    /** When player n°humanId, where humanId is an index starting at 1. */
    public final Rs whenPX(int humanId) {
        int idx = humanId - 1;
        return whenAuthenticated(makeName(idx), makePwd(idx));
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
    
    /** Convert single object to JSON. */
    @SneakyThrows(IOException.class)
    public final <T> T readValue(Response content, Class<T> valueType) {
        return Tools.JSON.readValue(content.asString(), valueType);
    }
    
    /** Convert generic paged objects to JSON. */
    @SneakyThrows(IOException.class)
    public final <T> Page<T> readPage(Response content, Class<T> parameterClass) {
        return Tools.JSON.readValue(content.asString(), Tools.JSON.getTypeFactory().constructParametricType(Page.class, parameterClass));
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
