package manon.user.service;

import manon.user.UserExistsException;
import manon.user.document.User;

public interface UserAdminService {
    
    User ensureAdmin() throws UserExistsException;
}
