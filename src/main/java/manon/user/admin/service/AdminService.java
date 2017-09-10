package manon.user.admin.service;

import lombok.RequiredArgsConstructor;
import manon.user.UserExistsException;
import manon.user.document.User;
import manon.user.registration.service.RegistrationService;
import manon.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("AdminService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminService {
    
    private final UserService userService;
    private final RegistrationService registrationService;
    
    @Value("${manon.admin.defaultAdmin.username}")
    private String adminUsername;
    
    @Value("${manon.admin.defaultAdmin.password}")
    private String adminPassword;
    
    public User ensureAdmin() throws UserExistsException {
        Optional<User> opAdmin = userService.findByUsername(adminUsername);
        if (opAdmin.isPresent()) {
            return opAdmin.get();
        }
        return registrationService.registerRoot(adminUsername, adminPassword);
    }
}
