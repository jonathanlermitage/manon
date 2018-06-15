package manon.user.service;

import manon.user.document.User;
import manon.util.basetest.InitBeforeTest;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RegistrationServiceTest extends InitBeforeTest {
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Value("${manon.admin.default-admin.username}")
    private String adminUsername;
    
    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(adminUsername).blockOptional().orElseThrow(Exception::new);
        User ensuredAdmin = registrationService.ensureAdmin();
        assertEquals(ensuredAdmin, existingAdmin);
    }
    
    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        clearDb();
        assertFalse(userService.findByUsername(adminUsername).blockOptional().isPresent());
        User ensuredAdmin = registrationService.ensureAdmin();
        assertEquals(ensuredAdmin, userService.findByUsername(adminUsername).blockOptional().orElseThrow(Exception::new));
    }
}
