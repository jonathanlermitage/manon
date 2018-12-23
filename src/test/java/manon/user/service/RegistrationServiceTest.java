package manon.user.service;

import manon.user.document.User;
import manon.util.basetest.AbstractInitBeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RegistrationServiceTest extends AbstractInitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new);
        User ensuredAdmin = registrationService.ensureAdmin();
        assertEquals(ensuredAdmin, existingAdmin);
    }
    
    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        clearDb();
        assertFalse(userService.findByUsername(cfg.getAdminDefaultAdminUsername()).isPresent());
        User ensuredAdmin = registrationService.ensureAdmin();
        assertEquals(ensuredAdmin, userService.findByUsername(cfg.getAdminDefaultAdminUsername()).orElseThrow(Exception::new));
    }
}
