package manon.util.basetest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import manon.Application;
import manon.profile.service.ProfileService;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.admin.service.AdminService;
import manon.user.document.User;
import manon.user.registration.service.RegistrationService;
import manon.user.service.UserService;
import manon.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static manon.app.config.API.API_V1;
import static manon.util.Tools.objId;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testng.Assert.fail;

/**
 * Recreate data beforeMethod test class.
 * To recreate data beforeMethod every test method, see {@link InitBeforeTest}.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ContextConfiguration(locations = {"classpath:/spring-context.xml"})
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
@Slf4j
public abstract class InitBeforeClass extends AbstractTestNGSpringContextTests {
    
    public final String TEST_API_PROFILE = "/profile";
    public final String TEST_API_USER = "/user";
    public final String TEST_API_LOBBY = "/lobby";
    
    @Setter
    private boolean initialized = false;
    
    @LocalServerPort
    private int port;
    
    @Getter
    private String apiV1;
    
    @Autowired
    protected AdminService adminService;
    @Autowired
    protected RegistrationService registrationService;
    @Autowired
    protected UserService userService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    protected ProfileService profileService;
    
    private final List<TestProfile> profiles = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, User> userCache = new ConcurrentHashMap<>();
    
    public final String ADMIN_NAME = "ROOT"; // IMPORTANT must reflect application.yml:manon.admin.defaultAdmin.username
    public final String ADMIN_PWD = "woot"; // IMPORTANT must reflect application.yml:manon.admin.defaultAdmin.password
    
    public final String UNKNOWN_USER_ID = objId();
    public long NUMBER_OF_USERS;
    
    public int getNumberOfProfiles() {
        return 2;
    }
    
    @BeforeClass
    public void beforeClass() {
        initialized = false;
        RestAssured.config = RestAssured.config.encoderConfig(EncoderConfig.encoderConfig()
                .defaultContentCharset("UTF-8"));
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        apiV1 = "http://localhost:" + port + API_V1;
        if (!initialized) {
            initDb();
            initialized = true;
        }
    }
    
    public void initDb() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        
        for (String cn : mongoTemplate.getDb().getCollectionNames()) {
            mongoTemplate.dropCollection(cn);
        }
        
        profiles.clear();
        userCache.clear();
        Set<Runnable> tasks = new HashSet<>();
        tasks.add(() -> {
            try {
                adminService.ensureAdmin();
            } catch (UserExistsException e) {
                log.error("", e);
            }
        });
        
        for (int i = 0; i < getNumberOfProfiles(); i++) {
            final int idx = i;
            tasks.add(() -> addProfile(idx));
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(tasks.size());
        tasks.forEach(taskExecutor::execute);
        taskExecutor.shutdown();
        while (!taskExecutor.isTerminated()) {
            taskExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }
        
        NUMBER_OF_USERS = profileService.count();
        
        log.debug("(Unit Test) called initDb from test class {}, took {} ms", this.getClass().getSimpleName(), System.currentTimeMillis() - t1);
    }
    
    private String makeName(int idx) {
        return "username" + idx;
    }
    
    private String makePwd(int idx) {
        return "p4ssw0rd" + idx;
    }
    
    private void addProfile(int idx) {
        try {
            User user = registrationService.registerPlayer(makeName(idx), makePwd(idx));
            registrationService.activate(user.getId());
            profiles.add(new TestProfile(user.getId(), user.getProfileId(), user.getUsername(), user.getPassword()));
        } catch (UserExistsException | UserNotFoundException e) {
            log.error("", e);
            fail("(init) can't add profile '" + makeName(idx) + "': " + e.getMessage());
        }
    }
    
    @AfterClass
    public void afterClass() {
        setInitialized(false);
    }
    
    //
    // Helpers: get generated test users and authenticate with their credentials
    //
    
    public Rs whenAnonymous() {
        return new Rs(RestAssured.given().authentication().none(), "", "");
    }
    
    public Rs whenAdmin() {
        return new Rs(RestAssured.given().authentication().basic(ADMIN_NAME, ADMIN_PWD), ADMIN_NAME, ADMIN_PWD);
    }
    
    /** When player n°humanIdx, where humanIdx is an index starting at 1. */
    public Rs whenPX(int humanIdx) {
        int idx = humanIdx - 1;
        return new Rs(RestAssured.given().authentication().basic(
                makeName(idx), makePwd(idx)),
                makeName(idx), makePwd(idx));
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
    public String pwd(int humanIdx) {
        return makePwd(humanIdx - 1);
    }
    
    /** Get name of player n°humanIdx, where humanIdx is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    public String name(int humanIdx) {
        return makeName(humanIdx - 1);
    }
    
    /** Get profile id of player n°humanIdx, where humanIdx is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    public String profileId(int humanIdx) {
        return findAndCacheUserByHumanIdx(humanIdx).getProfileId();
    }
    
    /** Get user id of player n°humanIdx, where humanIdx is an index starting at 1. */
    @SuppressWarnings("SameParameterValue")
    public String userid(int humanIdx) {
        return findAndCacheUserByHumanIdx(humanIdx).getId();
    }
    
    private User findAndCacheUserByHumanIdx(int humanIdx) {
        int idx = humanIdx - 1;
        if (!userCache.containsKey(idx)) {
            Optional<User> user = userService.findByUsername(makeName(idx));
            if (user.isPresent()) {
                userCache.put(idx, user.get());
            } else {
                fail("(init) can't find profile name '" + makeName(idx) + "'");
            }
        }
        return userCache.get(idx);
    }
    
    //
    // DataProviders
    //
    
    public final String DP_RS_USERS = "dataProviderRsUsers";
    
    @DataProvider
    public Object[][] dataProviderRsUsers() {
        return new Object[][]{
                {whenAdmin()},
                {whenP1()}
        };
    }
    
    public final String DP_RS_USERS_NO_ADMIN = "dataProviderRsUsersNonAdmin";
    
    @DataProvider
    public Object[][] dataProviderRsUsersNonAdmin() {
        return new Object[][]{
                {whenP1()}
        };
    }
    
    public final String DP_RS_ADMINS = "dataProviderRsAdmins";
    
    @DataProvider
    public Object[][] dataProviderRsAdmins() {
        return new Object[][]{
                {whenAdmin()},
        };
    }
    
    public final String DP_TRUEFALSE = "dataProviderTrueFalse";
    
    @DataProvider
    public Object[][] dataProviderTrueFalse() {
        return new Object[][]{
                {true},
                {false}
        };
    }
    
    //
    // Utils
    //
    
    @SneakyThrows(IOException.class)
    public <T> T readValue(String content, Class<T> valueType) {
        return Tools.JSON.readValue(content, valueType);
    }
}
