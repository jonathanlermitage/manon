package manon.user.service;

import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    
    /**
     * Get all users.
     * @param pageable page request.
     * @return paginated list of users.
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * Get a user by username.
     * @param username user username.
     * @return user.
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Get a user by username.
     * @param username user username.
     * @return user.
     */
    User readByUsername(String username)
            throws UsernameNotFoundException;
    
    /**
     * Get a user by id.
     * @param id user id.
     * @return user.
     */
    User readOne(String id)
            throws UserNotFoundException;
    
    /**
     * Create a new user and its profile.
     * @param user user data.
     * @return new user.
     */
    User create(User user)
            throws UserExistsException;
    
    void setPassword(String id, String password)
            throws UserNotFoundException;
    
    void setRegistrationState(String id, RegistrationStateEnum registrationState)
            throws UserNotFoundException;
}
