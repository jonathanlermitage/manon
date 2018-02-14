package manon.user.service;

import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.document.UserVersion;
import manon.user.form.UserFieldEnum;
import manon.user.form.UserUpdateForm;
import manon.user.registration.RegistrationStateEnum;
import manon.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    
    long count();
    
    void save(User user);
    
    void ensureExist(String... ids) throws UserNotFoundException;
    
    User readOne(String id) throws UserNotFoundException;
    
    User readByUsername(String username) throws UsernameNotFoundException;
    
    Optional<User> findByUsername(String username);
    
    UserVersion readVersionById(String id) throws UserNotFoundException;
    
    /**
     * Update a user's field.
     * @see UserRepository#updateField(String, UserFieldEnum, Object)
     * @param userId user id.
     * @param userUpdateForm field name and its new value.
     */
    void update(String userId, UserUpdateForm userUpdateForm);
    
    Page<User> findAll(Pageable pageable);
    
    /**
     * Create a new user and its user.
     * @param user user data.
     * @return new user.
     */
    User create(User user) throws UserExistsException;
    
    void setPassword(String id, String password);
    
    void setRegistrationState(String id, RegistrationStateEnum registrationState);
}
