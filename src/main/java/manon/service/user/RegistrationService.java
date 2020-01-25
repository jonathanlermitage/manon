package manon.service.user;

import manon.document.user.UserEntity;

public interface RegistrationService {

    /**
     * Activate a user.
     * @param userId user id.
     * @return user.
     */
    UserEntity activate(long userId);

    /**
     * Ban a user.
     * @param userId user id.
     * @return user.
     */
    UserEntity ban(long userId);

    /**
     * Suspend a user.
     * @param userId user id.
     * @return user.
     */
    UserEntity suspend(long userId);

    /**
     * Delete a user.
     * @param userId user id.
     */
    UserEntity delete(long userId);

    /**
     * Register a user.
     * Activation is pending.
     * @param username username.
     * @param password password.
     * @return user.
     */
    UserEntity registerPlayer(String username, String password);

    /**
     * Register actuator-user if absent.
     * @return existing actuator-user, otherwise newly registered user.
     */
    UserEntity ensureActuator();

    /**
     * Register admin-user if absent.
     * @return existing admin-user, otherwise newly registered user.
     */
    UserEntity ensureAdmin();
}
