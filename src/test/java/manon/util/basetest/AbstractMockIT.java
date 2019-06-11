package manon.util.basetest;

import manon.api.app.PingWS;
import manon.api.batch.JobRunnerWS;
import manon.api.user.FriendshipWS;
import manon.api.user.UserAdminWS;
import manon.api.user.UserWS;
import manon.util.web.Rs;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Used to mock all controllers and test access rights or input validation.
 * Application starts with some users and one admin. Data is recreated before test class.
 */
public abstract class AbstractMockIT extends AbstractIT {
    
    private static final String BANNED_USERNAME = "BANNED";
    private static final String DELETED_USERNAME = "DELETED";
    private static final String SUSPENDED_USERNAME = "SUSPENDED";
    private static final String BANNED_REACTIVATED_USERNAME = "BANNED_REACTIVATED";
    private static final String DELETED_REACTIVATED_USERNAME = "DELETED_REACTIVATED";
    private static final String SUSPENDED_REACTIVATED_USERNAME = "SUSPENDED_REACTIVATED";
    private static final String PWD = "password" + currentTimeMillis();
    
    private boolean initialized = false;
    
    /** Clear data before test class, not before each test method. */
    @Override
    @BeforeEach
    public void clearData() throws Exception {
        if (!initialized) {
            super.clearData();
            initialized = true;
        }
    }
    
    @MockBean
    protected FriendshipWS friendshipWS;
    @MockBean
    protected JobRunnerWS jobRunnerWS;
    @MockBean
    protected PingWS pingWS;
    @MockBean
    protected UserAdminWS userAdminWS;
    @MockBean
    protected UserWS userWs;
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Override
    public void additionalParallelInitDb() throws Exception {
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
    
    @BeforeEach
    public void setUpMocks() {
        initMocks(this);
        Mockito.clearInvocations(friendshipWS);
        Mockito.clearInvocations(jobRunnerWS);
        Mockito.clearInvocations(pingWS);
        Mockito.clearInvocations(userAdminWS);
        Mockito.clearInvocations(userWs);
    }
    
    public <T> T verify(T mock, int status) {
        return verify(mock, status, 1);
    }
    
    public <T> T verify(T mock, int status, int times) {
        return Mockito.verify(mock, status > 199 && status < 300 ? Mockito.times(times) : Mockito.never());
    }
    
    private Rs whenUsername(String username) {
        return Rs.authenticated(username, PWD);
    }
    
    public final String DP_ALLOW_ADMIN = "dataProviderAllowAdmin";
    
    @SuppressWarnings("unused") // used via DP_ALLOW_ADMIN
    public Object[][] dataProviderAllowAdmin() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenActuator(), SC_FORBIDDEN},
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
    
    public final String DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS = "dataProviderAllowAuthenticateAndAnonymous";
    
    @SuppressWarnings("unused") // used via DP_ALLOW_AUTHENTICATED_AND_ANONYMOUS
    public Object[][] dataProviderAllowAuthenticateAndAnonymous() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenActuator(), SC_OK},
            {whenP1(), SC_OK},
            {whenAnonymous(), SC_OK},
            {whenUsername(BANNED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(DELETED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(SUSPENDED_REACTIVATED_USERNAME), SC_OK},
            {whenUsername(BANNED_USERNAME), SC_OK},
            {whenUsername(DELETED_USERNAME), SC_OK},
            {whenUsername(SUSPENDED_USERNAME), SC_OK}
        };
    }
    
    public final String DP_ALLOW_AUTHENTICATED = "dataProviderAllowAuthenticated";
    
    @SuppressWarnings("unused") // used via DP_ALLOW_AUTHENTICATED
    public Object[][] dataProviderAllowAuthenticated() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenActuator(), SC_OK},
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
