package manon.app.security;

import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserloaderServiceTest extends AbstractInitBeforeClass {
    
    @Autowired
    private UserLoaderService userLoaderService;
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Test
    public void shouldLoadUserByUsername() {
        String username = name(1);
        UserSimpleDetails userSimpleDetailsFound = userLoaderService.loadUserByUsername(username);
        assertThat(userSimpleDetailsFound).isNotNull();
        assertThat(userSimpleDetailsFound.getUsername()).isEqualTo(username);
        assertThat(userSimpleDetailsFound.getUser().getUsername()).isEqualTo(username);
    }
    
    @Test(expectedExceptions = UsernameNotFoundException.class)
    public void shouldFailLoadUserByUnknownUsername() {
        userLoaderService.loadUserByUsername(UNKNOWN_USER_NAME);
    }
    
    @Test(expectedExceptions = UsernameNotFoundException.class)
    public void shouldFailLoadUserByNullUsername() {
        userLoaderService.loadUserByUsername(null);
    }
}
