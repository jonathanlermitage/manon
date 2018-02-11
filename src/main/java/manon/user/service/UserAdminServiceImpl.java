package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.UserExistsException;
import manon.user.document.User;
import manon.user.registration.service.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {
    
    private final UserService userService;
    private final RegistrationService registrationService;
    
    @Value("${manon.admin.default-admin.username}")
    private String adminUsername;
    
    @Value("${manon.admin.default-admin.password}")
    private String adminPassword;
    
    @Override
    public User ensureAdmin() throws UserExistsException {
        Optional<User> opAdmin = userService.findByUsername(adminUsername);
        if (opAdmin.isPresent()) {
            return opAdmin.get();
        }
        return registrationService.registerRoot(adminUsername, adminPassword);
    }
}
