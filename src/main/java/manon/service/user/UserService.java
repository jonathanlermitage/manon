package manon.service.user;

import manon.document.user.User;
import manon.document.user.UserIdProjection;
import manon.document.user.UserVersionProjection;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.model.user.RegistrationState;
import manon.model.user.form.UserUpdateForm;
import manon.util.ExistForTesting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    
    User readOne(long id) throws UserNotFoundException;
    
    User readOneAndFetchUserSnapshots(long id) throws UserNotFoundException;
    
    User readByUsername(String username) throws UserNotFoundException;
    
    Optional<User> findByUsername(String username);
    
    UserVersionProjection readVersionById(long id) throws UserNotFoundException;
    
    /**
     * Update a user's data.
     * @param userId user id.
     * @param userUpdateForm user data.
     */
    void update(long userId, UserUpdateForm userUpdateForm);
    
    Page<User> findAll(Pageable pageable);
    
    /**
     * Create a new user and its user.
     * @param user user data.
     * @return new user.
     */
    User create(User user) throws UserExistsException;
    
    void encodeAndSetPassword(long id, String password);
    
    void setRegistrationState(long id, RegistrationState registrationState);
    
    /**
     * Check if a raw password validates a (BCrypt) encoded password.
     * @param rawPassword raw password.
     * @param encodedPassword encoded password; must not be null.
     */
    void validatePassword(String rawPassword, String encodedPassword) throws PasswordNotMatchException;
    
    @ExistForTesting
    long count();
    
    @ExistForTesting
    User save(User user);
    
    @ExistForTesting
    void existOrFail(long... ids) throws UserNotFoundException;
    
    @ExistForTesting
    UserIdProjection readIdByUsername(String username) throws UserNotFoundException;
    
    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
