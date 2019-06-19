package manon.util.basetest;

import lombok.extern.slf4j.Slf4j;
import manon.api.app.PingWS;
import manon.api.batch.JobRunnerWS;
import manon.api.user.AuthAdminWS;
import manon.api.user.AuthWS;
import manon.api.user.FriendshipWS;
import manon.api.user.UserAdminWS;
import manon.api.user.UserWS;
import manon.util.web.Rs;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static java.lang.System.currentTimeMillis;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Used to mock all controllers and test access rights or input validation.
 * Application starts with some users and one admin. Data is recreated before test class.
 */
@Slf4j
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
    public void clearData() {
        if (!initialized) {
            super.clearData();
            initialized = true;
        }
    }
    
    @SpyBean // not mocked because its needed by security
    protected AuthWS authWS;
    
    @MockBean
    protected AuthAdminWS authAdminWS;
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
    public void additionalParallelInitDb() {
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
        Mockito.clearInvocations(authAdminWS);
        Mockito.clearInvocations(authWS);
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
        return Rs.authenticated(username, PWD, jwtTokenService.generateToken(username));
    }
    
    public final Rs whenBannedReactivated() {
        return whenUsername(BANNED_REACTIVATED_USERNAME);
    }
    
    public final Rs whenDeletedReactivated() {
        return whenUsername(DELETED_REACTIVATED_USERNAME);
    }
    
    public final Rs whenSuspendedReactivated() {
        return whenUsername(SUSPENDED_REACTIVATED_USERNAME);
    }
    
    public final Rs whenBanned() {
        return whenUsername(BANNED_USERNAME);
    }
    
    public final Rs whenDeleted() {
        return whenUsername(DELETED_USERNAME);
    }
    
    public final Rs whenSuspended() {
        return whenUsername(SUSPENDED_USERNAME);
    }
    
    public final String DP_ALLOW_ADMIN = "dataProviderAllowAdmin";
    
    @SuppressWarnings("unused") // used via DP_ALLOW_ADMIN
    public Object[][] dataProviderAllowAdmin() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenActuator(), SC_FORBIDDEN},
            {whenP1(), SC_FORBIDDEN},
            {whenAnonymous(), SC_UNAUTHORIZED},
            {whenBannedReactivated(), SC_FORBIDDEN},
            {whenDeletedReactivated(), SC_FORBIDDEN},
            {whenSuspendedReactivated(), SC_FORBIDDEN},
            {whenBanned(), SC_UNAUTHORIZED},
            {whenDeleted(), SC_UNAUTHORIZED},
            {whenSuspended(), SC_UNAUTHORIZED}
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
            {whenBannedReactivated(), SC_OK},
            {whenDeletedReactivated(), SC_OK},
            {whenSuspendedReactivated(), SC_OK},
            {whenBanned(), SC_OK},
            {whenDeleted(), SC_OK},
            {whenSuspended(), SC_OK}
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
            {whenBannedReactivated(), SC_OK},
            {whenDeletedReactivated(), SC_OK},
            {whenSuspendedReactivated(), SC_OK},
            {whenBanned(), SC_UNAUTHORIZED},
            {whenDeleted(), SC_UNAUTHORIZED},
            {whenSuspended(), SC_UNAUTHORIZED}
        };
    }
}
