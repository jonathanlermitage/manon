package manon.user.registration.service;

import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;

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
    void delete(String userId) throws UserNotFoundException;
    
    /**
     * Register a user.
     * Activation is pending.
     * @param username username.
     * @param password password.
     * @return user.
     */
    User registerPlayer(String username, String password) throws UserExistsException;
    
    /**
     * Register an admin (root).
     * Activation is already done.
     * @param username username.
     * @param password password.
     * @return admin user.
     */
    User registerRoot(String username, String password) throws UserExistsException;
    
    /**
     * Register an admin (root) is absent.
     * @return existing admin user, otherwise newly registered admin user.
     */
    User ensureAdmin() throws UserExistsException;
}
