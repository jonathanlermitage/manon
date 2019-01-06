package manon.app.security;

import manon.util.basetest.AbstractInitBeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserloaderServiceIntegrationTest extends AbstractInitBeforeClass {
    
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
    
    @Test
    public void shouldFailLoadUserByUnknownUsername() {
        assertThatThrownBy(() -> userLoaderService.loadUserByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UsernameNotFoundException.class);
    }
    
    @Test
    public void shouldFailLoadUserByNullUsername() {
        assertThatThrownBy(() -> userLoaderService.loadUserByUsername(null))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
