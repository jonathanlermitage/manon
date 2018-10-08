package manon.util.basetest;

import manon.app.info.api.InfoWS;
import manon.user.api.FriendshipWS;
import manon.user.api.UserAdminWS;
import manon.user.api.UserWS;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Used to mock services.
 */
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public abstract class MockBeforeClass extends InitBeforeClass {
    
    @MockBean
    protected FriendshipWS friendshipWS;
    @MockBean
    protected InfoWS infoWS;
    @MockBean
    protected UserAdminWS userAdminWS;
    @MockBean
    protected UserWS userWs;
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @BeforeMethod
    public void setUpMocks() {
        initMocks(this);
        Mockito.clearInvocations(friendshipWS);
        Mockito.clearInvocations(infoWS);
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
    
    public final String DP_ALLOW_ADMIN = "dataProviderAllowAdmin";
    
    @DataProvider
    public Object[][] dataProviderAllowAdmin() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenP1(), SC_FORBIDDEN},
            {whenAnonymous(), SC_UNAUTHORIZED}
        };
    }
    
    public final String DP_ALLOW_AUTHENTICATED = "dataProviderAllowAuthenticated";
    
    @DataProvider
    public Object[][] dataProviderAllowAuthenticated() {
        return new Object[][]{
            {whenAdmin(), SC_OK},
            {whenP1(), SC_OK},
            {whenAnonymous(), SC_UNAUTHORIZED}
        };
    }
}
