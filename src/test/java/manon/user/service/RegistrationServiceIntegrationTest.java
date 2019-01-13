package manon.user.service;

import manon.user.document.User;
import manon.util.basetest.AbstractInitBeforeTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationServiceIntegrationTest extends AbstractInitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new);
        User ensuredAdmin = registrationService.ensureAdmin();
        assertThat(ensuredAdmin).isEqualTo(existingAdmin);
    }
    
    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        clearDb();
        assertThat(userService.findByUsername(cfg.getAdminDefaultAdminUsername())).isNotPresent();
        User ensuredAdmin = registrationService.ensureAdmin();
        assertThat(ensuredAdmin).isEqualTo(userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new));
    }
}
