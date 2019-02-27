package manon.app.security;

import manon.util.basetest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserDetailsServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private UserDetailsServiceImpl UserDetailsService;
    
    @Override
    public int getNumberOfUsers() {
        return 1;
    }
    
    @Test
    public void shouldLoadUserByUsername() {
        String username = name(1);
        UserSimpleDetails userSimpleDetailsFound = UserDetailsService.loadUserByUsername(username);
        assertThat(userSimpleDetailsFound).isNotNull();
        assertThat(userSimpleDetailsFound.getUsername()).isEqualTo(username);
        assertThat(userSimpleDetailsFound.getUser().getUsername()).isEqualTo(username);
    }
    
    @Test
    public void shouldFailLoadUserByUnknownUsername() {
        assertThatThrownBy(() -> UserDetailsService.loadUserByUsername(UNKNOWN_USER_NAME))
            .isInstanceOf(UsernameNotFoundException.class);
    }
    
    @Test
    public void shouldFailLoadUserByNullUsername() {
        assertThatThrownBy(() -> UserDetailsService.loadUserByUsername(null))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
