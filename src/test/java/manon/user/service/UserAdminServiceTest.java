package manon.user.service;

import manon.user.document.User;
import manon.util.basetest.InitBeforeTest;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class UserAdminServiceTest extends InitBeforeTest {
    
    @Value("${manon.admin.default-admin.username}")
    private String adminUsername;
    
    @Override
    public int getNumberOfUsers() {
        return 0;
    }
    
    @Test
    public void shouldEnsureExistingAdmin() throws Exception {
        User existingAdmin = userService.findByUsername(adminUsername).orElseThrow(Exception::new);
        User ensuredAdmin = userAdminService.ensureAdmin();
        assertEquals(ensuredAdmin, existingAdmin);
    }
    
    @Test
    public void shouldEnsureNewAdminIfAbsent() throws Exception {
        clearDb();
        assertFalse(userService.findByUsername(adminUsername).isPresent());
        User ensuredAdmin = userAdminService.ensureAdmin();
        assertEquals(ensuredAdmin, userService.findByUsername(adminUsername).orElseThrow(Exception::new));
    }
}
