package manon.service.user.impl;

import manon.document.user.UserEntity;
import manon.util.basetest.AbstractNoUserIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RegistrationServiceIT extends AbstractNoUserIT {

    @Test
    void shouldEnsureExistingAdmin() throws Exception {
        UserEntity existingAdmin = userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new);
        UserEntity ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin).isEqualTo(existingAdmin);
    }

    @Test
    void shouldEnsureNewAdminIfAbsent() throws Exception {
        UserEntity previousAdmin = userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new);
        userService.deleteAll();
        Assertions.assertThat(userService.findByUsername(cfg.getDefaultUserAdminUsername())).isNotPresent();
        UserEntity ensuredAdmin = registrationService.ensureAdmin();
        Assertions.assertThat(ensuredAdmin)
            .isNotEqualTo(previousAdmin)
            .isEqualTo(userService.findByUsername(cfg.getDefaultUserAdminUsername()).orElseThrow(Exception::new));
    }
}
