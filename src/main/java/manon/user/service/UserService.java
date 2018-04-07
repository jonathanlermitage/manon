package manon.user.service;

import manon.user.document.User;
import manon.user.document.UserVersion;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.form.UserUpdateForm;
import manon.user.model.RegistrationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    
    long count();
    
    void save(User user);
    
    void existOrFail(String... ids) throws UserNotFoundException;
    
    User readOne(String id) throws UserNotFoundException;
    
    User readByUsername(String username) throws UsernameNotFoundException;
    
    Optional<User> findByUsername(String username);
    
    UserVersion readVersionById(String id) throws UserNotFoundException;
    
    /**
     * Update a user's data.
     * @param userId user id.
     * @param userUpdateForm user data.
     */
    void update(String userId, UserUpdateForm userUpdateForm);
    
    Page<User> findAll(Pageable pageable);
    
    /**
     * Create a new user and its user.
     * @param user user data.
     * @return new user.
     */
    User create(User user) throws UserExistsException;
    
    void encodeAndSetPassword(String id, String password);
    
    void setRegistrationState(String id, RegistrationState registrationState);
}
