package manon.service.user.impl;

import manon.document.user.User;
import manon.util.basetest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistrationServiceIntegrationTest extends AbstractIntegrationTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new);
        User ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin).isEqualTo(existingAdmin);
    }
    
    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        User previousAdmin = userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new);
        userRepository.deleteAll();
        Assertions.assertThat(userService.findByUsername(cfg.getAdminDefaultAdminUsername())).isNotPresent();
        User ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin)
            .isNotEqualTo(previousAdmin)
            .isEqualTo(userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new));
    }
}