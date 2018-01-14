package manon.util.basetest;

import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Used to mock services.
 */
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public abstract class MockBeforeClass extends InitBeforeClass {
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @BeforeMethod
    public void setUpMocks() {
        initMocks(this);
    }
    
    public <T> T verify(T mock, int status) {
        return verify(mock, status, 1);
    }
    
    public <T> T verify(T mock, int status, int times) {
        return Mockito.verify(mock, status > 199 && status < 300 ? Mockito.times(times) : Mockito.never());
    }
    
    public final String FAKE_ID = Integer.toString(Integer.MAX_VALUE);
    public final boolean FAKE_BOOL = true;
    
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
    
    public final String DP_ALLOW_AUTHENTICATED_201 = "dataProviderAllowAuthenticated201";
    
    @DataProvider
    public Object[][] dataProviderAllowAuthenticated201() {
        return new Object[][]{
                {whenAdmin(), SC_CREATED},
                {whenP1(), SC_CREATED},
                {whenAnonymous(), SC_UNAUTHORIZED}
        };
    }
}
