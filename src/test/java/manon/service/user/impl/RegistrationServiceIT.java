package manon.service.user.impl;

import manon.document.user.User;
import manon.util.basetest.AbstractNoUserIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistrationServiceIT extends AbstractNoUserIT {

    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new);
        User ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin).isEqualTo(existingAdmin);
    }

    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        User previousAdmin = userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new);
        userService.deleteAll();
        Assertions.assertThat(userService.findByUsername(cfg.getDefaultUserAdminUsername())).isNotPresent();
        User ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin)
            .isNotEqualTo(previousAdmin)
            .isEqualTo(userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new));
    }
}
