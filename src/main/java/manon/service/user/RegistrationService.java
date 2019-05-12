package manon.service.user;

import manon.document.user.User;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;

public interface RegistrationService {
    
    /**
     * Activate a user.
     * @param userId user id.
     * @return user.
     */
    User activate(long userId) throws UserNotFoundException;
    
    /**
     * Ban a user.
     * @param userId user id.
     * @return user.
     */
    User ban(long userId) throws UserNotFoundException;
    
    /**
     * Suspend a user.
     * @param userId user id.
     * @return user.
     */
    User suspend(long userId) throws UserNotFoundException;
    
    /**
     * Delete a user.
     * @param userId user id.
     */
    User delete(long userId) throws UserNotFoundException;
    
    /**
     * Register a user.
     * Activation is pending.
     * @param username username.
     * @param password password.
     * @return user.
     */
    User registerPlayer(String username, String password) throws UserExistsException;
    
    /**
     * Register actuator-user if absent.
     * @return existing actuator-user, otherwise newly registered user.
     */
    User ensureActuator();
    
    /**
     * Register admin-user if absent.
     * @return existing admin-user, otherwise newly registered user.
     */
    User ensureAdmin();
}
