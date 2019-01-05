package manon.user.service;

import manon.user.document.User;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;

public interface RegistrationService {
    
    /**
     * Activate a user.
     * @param userId user id.
     * @return user.
     */
    User activate(String userId) throws UserNotFoundException;
    
    /**
     * Ban a user.
     * @param userId user id.
     * @return user.
     */
    User ban(String userId) throws UserNotFoundException;
    
    /**
     * Suspend a user.
     * @param userId user id.
     * @return user.
     */
    User suspend(String userId) throws UserNotFoundException;
    
    /**
     * Delete a user.
     * @param userId user id.
     */
    User delete(String userId) throws UserNotFoundException;
    
    /**
     * Register a user.
     * Activation is pending.
     * @param username username.
     * @param password password.
     * @return user.
     */
    User registerPlayer(String username, String password) throws UserExistsException;
    
    /**
     * Register an admin (root) is absent.
     * @return existing admin user, otherwise newly registered admin user.
     */
    User ensureAdmin() throws UserExistsException;
}
