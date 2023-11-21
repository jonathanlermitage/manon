package manon.util.basetest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.Application;
import manon.app.Cfg;
import manon.document.user.UserEntity;
import manon.err.user.UserNotFoundException;
import manon.service.app.AuthTokenService;
import manon.service.app.JwtTokenService;
import manon.service.app.PerformanceRecorder;
import manon.service.app.PingService;
import manon.service.app.TrxDemoService;
import manon.service.batch.JobRunnerService;
import manon.service.user.FriendshipEventService;
import manon.service.user.FriendshipRequestService;
import manon.service.user.FriendshipService;
import manon.service.user.PasswordEncoderService;
import manon.service.user.RegistrationService;
import manon.service.user.UserService;
import manon.service.user.UserSnapshotService;
import manon.service.user.UserStatsService;
import manon.util.TestTools;
import manon.util.web.AuthMode;
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
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
@AutoConfigureObservability // make sure actuator prometheus endpoint is enabled during tests
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, MockitoTestExecutionListener.class})
public abstract class AbstractIT {

    @LocalServerPort
    private int port;

    @Autowired(required = false)
    protected PerformanceRecorder performanceRecorder;

    //<editor-fold desc="beans">
    @Autowired
    protected Cfg cfg;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private JavaMailSender mailSender;

    @SpyBean
    protected AuthTokenService authTokenService;
    @SpyBean
    protected FriendshipService friendshipService;
    @SpyBean
    protected FriendshipEventService friendshipEventService;
    @SpyBean
    protected FriendshipRequestService friendshipRequestService;
    @SpyBean
    protected JobRunnerService jobRunnerService;
    @SpyBean
    protected JwtTokenService jwtTokenService;
    @SpyBean
    protected PasswordEncoderService passwordEncoderService;
    @SpyBean
    protected PingService pingService;
    @SpyBean
    protected RegistrationService registrationService;
    @SpyBean
    protected TrxDemoService trxDemoService;
    @SpyBean
    protected UserDetailsService userDetailsService;
    @SpyBean
    protected UserService userService;
    @SpyBean
    protected UserSnapshotService userSnapshotService;
    @SpyBean
    protected UserStatsService userStatsService;
    //</editor-fold>

    private final String ENV = "junit";
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

    public GreenMail greenMail;

    public int userCount;

    public int getNumberOfUsers() {
        return 2;
    }

    public AuthMode getAuthMode() {
        return AuthMode.REGULAR_VIA_API;
    }

    @BeforeAll
    public final void initRestAssured() {
        RestAssured.config.encoderConfig(encoderConfig().defaultContentCharset(StandardCharsets.UTF_8));
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        Rs.tokenProvider = jwtTokenService;
    }

    /** Clear data before each test method. Do NOT override it in non-abstract test classes. */
    @BeforeEach
    public void clearData() {
        userIdCache.clear();
        initDb();
        beforeEachTestOnceDbPopulated();

        Mockito.clearInvocations(authTokenService);
        Mockito.clearInvocations(friendshipService);
        Mockito.clearInvocations(friendshipEventService);
        Mockito.clearInvocations(friendshipRequestService);
        Mockito.clearInvocations(jobRunnerService);
        Mockito.clearInvocations(jwtTokenService);
        Mockito.clearInvocations(passwordEncoderService);
        Mockito.clearInvocations(pingService);
        Mockito.clearInvocations(registrationService);
        Mockito.clearInvocations(trxDemoService);
        Mockito.clearInvocations(userDetailsService);
        Mockito.clearInvocations(userService);
        Mockito.clearInvocations(userSnapshotService);
        Mockito.clearInvocations(userStatsService);
    }

    /** Execute code before each test, after database initialization. */
    public void beforeEachTestOnceDbPopulated() {
        // n/a
    }

    private void clearDb() {
        authTokenService.deleteAll();

        friendshipEventService.deleteAll();
        friendshipRequestService.deleteAll();
        friendshipService.deleteAll();

        userSnapshotService.deleteAll();
        userStatsService.deleteAll();

        userService.deleteAll();
    }

    private void clearCache() {
        authTokenService.evictAllCache();
    }

    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    public void initDb() {
        long t1 = currentTimeMillis();
        MDC.put(KEY_ENV, ENV);
        clearDb();
        clearCache();

        Executor tasksExecutor = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> registerPlayerTask = CompletableFuture.runAsync(() -> {
            for (int idx = 0; idx < getNumberOfUsers(); idx++) {
                registrationService.registerPlayer(makeName(idx), makePwd(idx));
            }
        }, tasksExecutor);
        CompletableFuture<Void> ensureUsersTask = CompletableFuture.runAsync(() -> {
            registrationService.ensureActuator();
            registrationService.ensureAdmin();
        }, tasksExecutor);
        CompletableFuture<Void> additionalInitDbTask = CompletableFuture.runAsync(this::additionalParallelInitDb, tasksExecutor);
        CompletableFuture.allOf(registerPlayerTask, ensureUsersTask, additionalInitDbTask).get();

        userCount = (int) userService.count();

        log.debug("initDb from class {} took {} ms", this.getClass().getSimpleName(), currentTimeMillis() - t1);
        MDC.clear();
    }

    @BeforeAll
    public final void startFakeMailServer() {
        MDC.put(KEY_ENV, ENV);
        log.debug("starting GreenMail server...");
        int freeSmtpPort = TestTools.findFreePort();
        ((JavaMailSenderImpl) mailSender).setPort(freeSmtpPort);
        ServerSetup serverSetup = new ServerSetup(freeSmtpPort, cfg.getMailHost(), ServerSetup.PROTOCOL_SMTP);
        serverSetup.setVerbose(false);
        greenMail = new GreenMail(serverSetup);
        greenMail.setUser(cfg.getMailUsername(), cfg.getMailPassword());
        if (log.isDebugEnabled()) {
            log.debug("GreenMail config: " + greenMail.getSmtp().getServerSetup().toString());
        }
        greenMail.start();
        log.debug("started GreenMail server");
        MDC.clear();
    }

    @AfterAll
    public final void stopFakeMailServer() throws FolderException {
        MDC.put(KEY_ENV, ENV);
        log.debug("stopping GreenMail server...");
        greenMail.purgeEmailFromAllMailboxes();
        greenMail.stop();
        log.debug("stopped GreenMail server");
        MDC.clear();
    }

    @BeforeEach
    public void resetFakeMailServer() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    /** Override do add logic that occurs at the end of the {@link #initDb()}. */
    public void additionalParallelInitDb() {
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
        MDC.put(KEY_ENV, ENV);
        if (performanceRecorder != null && !performanceRecorder.getStats().isEmpty()) {
            log.info(performanceRecorder.getStats());
        }
        logGCStats();
        logMemoryStats();
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
        log.info("GC stats:\n - total GC collections: {}\n - total GC collection time: {}ms (activity ratio: {}%)\n - JVM uptime: {}ms",
            totalGarbageCollections, garbageCollectionTime, gcActivityRatio, uptime);
        assertThat(gcActivityRatio).as("GC pressure should be low (less than 10% execution time)").isLessThan(10);
    }

    private void logMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long mb = 1024 * 1024;
        log.info("Memory stats:\n - free memory: {}MB,\n - allocated memory: {}MB\n - max memory: {}MB\n - total free memory: {}MB",
            freeMemory / mb,
            allocatedMemory / mb,
            maxMemory / mb,
            (freeMemory + (maxMemory - allocatedMemory)) / mb);
    }

    //
    // Helpers: get generated test users and authenticate with their credentials
    //

    public final Response login(String username, String password) {
        return Rs.login(username, password);
    }

    public final String loginAndReturnToken(String username, String password) {
        return Rs.loginAndReturnToken(username, password);
    }

    public final RequestSpecification usingToken(String token) {
        return Rs.usingToken(token, getAuthMode()).getSpec();
    }

    public final Rs whenAnonymous() {
        return Rs.anonymous();
    }

    public final Rs whenAuthenticated(String username, String password) {
        return Rs.authenticated(username, password, getAuthMode());
    }

    public final Rs whenActuator() {
        return whenAuthenticated(cfg.getDefaultUserActuatorUsername(), cfg.getDefaultUserActuatorPassword());
    }

    public final Rs whenAdmin() {
        return whenAuthenticated(cfg.getDefaultUserAdminUsername(), cfg.getDefaultUserAdminPassword());
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
    public final UserEntity user(int humanId) {
        return userService.readByUsername(name(humanId));
    }

    /** Get user id of player n°humanId, where humanId is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    @SneakyThrows(UserNotFoundException.class)
    public final long userId(int humanId) {
        return findAndCacheUserIdByIdx(humanId - 1);
    }

    private long findAndCacheUserIdByIdx(int idx) {
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
        return objectMapper.readValue(content.asString(), valueType);
    }

    /** Convert generic paged objects to JSON. */
    @SneakyThrows(IOException.class)
    public final <T> Page<T> readPage(Response content, Class<T> parameterClass) {
        return objectMapper.readValue(content.asString(), objectMapper.getTypeFactory().constructParametricType(Page.class, parameterClass));
    }

    /** Compute a long string. */
    public final String veryLongString(String base) {
        return base.repeat(500);
    }

    /**
     * Read a resource file from classpath.
     * @param resource resource path, something like "your/file.ext".
     * @return file content.
     */
    @SneakyThrows(IOException.class)
    public String resource(String resource) {
        return new String(Files.readAllBytes(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + resource).toPath()));
    }
}
