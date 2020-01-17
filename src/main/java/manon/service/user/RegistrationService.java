package manon.service.user;

import manon.document.user.User;

public interface RegistrationService {

    /**
     * Activate a user.
     * @param userId user id.
     * @return user.
     */
    User activate(long userId);

    /**
     * Ban a user.
     * @param userId user id.
     * @return user.
     */
    User ban(long userId);

    /**
     * Suspend a user.
     * @param userId user id.
     * @return user.
     */
    User suspend(long userId);

    /**
     * Delete a user.
     * @param userId user id.
     */
    User delete(long userId);

    /**
     * Register a user.
     * Activation is pending.
     * @param username username.
     * @param password password.
     * @return user.
     */
    User registerPlayer(String username, String password);

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
