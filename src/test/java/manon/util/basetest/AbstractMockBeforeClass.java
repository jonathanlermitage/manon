package manon.util.basetest;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import manon.app.batch.api.TaskRunnerWS;
import manon.app.info.api.InfoWS;
import manon.user.api.FriendshipWS;
import manon.user.api.UserAdminWS;
import manon.user.api.UserWS;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.util.web.Rs;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Used to mock services.
 */
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public abstract class AbstractMockBeforeClass extends AbstractInitBeforeClass {
    
    private static final String BANNED_USERNAME = "BANNED";
    private static final String DELETED_USERNAME = "DELETED";
    private static final String SUSPENDED_USERNAME = "SUSPENDED";
    private static final String BANNED_REACTIVATED_USERNAME = "BANNED_REACTIVATED";
    private static final String DELETED_REACTIVATED_USERNAME = "DELETED_REACTIVATED";
    private static final String SUSPENDED_REACTIVATED_USERNAME = "SUSPENDED_REACTIVATED";
    private static final String PWD = "password" + System.currentTimeMillis();
    
    @MockBean
    protected FriendshipWS friendshipWS;
    @MockBean
    protected InfoWS infoWS;
    @MockBean
    protected TaskRunnerWS taskRunnerWS;
    @MockBean
    protected UserAdminWS userAdminWS;
    @MockBean
    protected UserWS userWs;
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Override
    public void additionalInitDb() throws UserExistsException, UserNotFoundException {
        registrationService.ban(registrationService.registerPlayer(BANNED_USERNAME, PWD).getId());
        registrationService.delete(registrationService.registerPlayer(DELETED_USERNAME, PWD).getId());
        registrationService.suspend(registrationService.registerPlayer(SUSPENDED_USERNAME, PWD).getId());
        registrationService.activate(
            registrationService.ban(registrationService.registerPlayer(BANNED_REACTIVATED_USERNAME, PWD).getId()).getId());
        registrationService.activate(
            registrationService.delete(registrationService.registerPlayer(DELETED_REACTIVATED_USERNAME, PWD).getId()).getId());
        registrationService.activate(
            registrationService.suspend(registrationService.registerPlayer(SUSPENDED_REACTIVATED_USERNAME, PWD).getId()).getId());
    }
    
    @BeforeMethod
    public void setUpMocks() {
        initMocks(this);
        Mockito.clearInvocations(friendshipWS);
        Mockito.clearInvocations(infoWS);
        Mockito.clearInvocations(taskRunnerWS);
        Mockito.clearInvocations(userAdminWS);
        Mockito.clearInvocations(userWs);
    }
    
    public <T> T verify(T mock, int status) {
        return verify(mock, status, 1);
    }
    
    public <T> T verify(T mock, int status, int times) {
        return Mockito.verify(mock, status > 199 && status < 300 ? Mockito.times(times) : Mockito.never());
    }
    
    public final String FAKE_ID = Integer.toString(Integer.MAX_VALUE);
    
    private Rs whenUsername(String username) {
        RequestSpecification rs = RestAssured.given()
            .header("X-Request-Id", username.toLowerCase() + "-" + currentTimeMillis())
            .auth().basic(username, PWD);
        return new Rs(rs, username, PWD);
    }
    
    public final String DP_ALLOW_ADMIN = "dataProviderAllowAdmin";
    
    @DataProvider
    public Object[][] dataProviderAllowAdmin() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenP1(), SC_FORBIDDEN},
            {whenAnonymous(), SC_UNAUTHORIZED},
            {whenUsername(BANNED_REACTIVATED_USERNAME), SC_FORBIDDEN},
            {whenUsername(DELETED_REACTIVATED_USERNAME), SC_FORBIDDEN},
            {whenUsername(SUSPENDED_REACTIVATED_USERNAME), SC_FORBIDDEN},
            {whenUsername(BANNED_USERNAME), SC_UNAUTHORIZED},
            {whenUsername(DELETED_USERNAME), SC_UNAUTHORIZED},
            {whenUsername(SUSPENDED_USERNAME), SC_UNAUTHORIZED}
        };
    }
    
    public final String DP_ALLOW_AUTHENTICATED = "dataProviderAllowAuthenticated";
    
    @DataProvider
    public Object[][] dataProviderAllowAuthenticated() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenP1(), SC_OK},
            {whenAnonymous(), SC_UNAUTHORIZED},
            {whenUsername(BANNED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(DELETED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(SUSPENDED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(BANNED_USERNAME), SC_UNAUTHORIZED},
            {whenUsername(DELETED_USERNAME), SC_UNAUTHORIZED},
            {whenUsername(SUSPENDED_USERNAME), SC_UNAUTHORIZED}
        };
    }
}
