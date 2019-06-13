package manon.service.user;

import manon.document.user.User;
import manon.document.user.UserIdProjection;
import manon.document.user.UserVersionProjection;
import manon.err.user.PasswordNotMatchException;
import manon.model.user.RegistrationState;
import manon.model.user.form.UserUpdateForm;
import manon.util.ExistForTesting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    
    User readOne(long id);
    
    User readOneAndFetchUserSnapshots(long id);
    
    User readByUsername(String username);
    
    Optional<User> findByUsername(String username);
    
    UserVersionProjection readVersionById(long id);
    
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
    User create(User user);
    
    void encodeAndSetPassword(long id, String password);
    
    void setRegistrationState(long id, RegistrationState registrationState);
    
    /**
     * Check if a raw password validates an encoded password, and throw an exception if validation failed.
     * @param rawPassword raw password.
     * @param encodedPassword encoded password.
     */
    void validatePassword(String rawPassword, String encodedPassword) throws PasswordNotMatchException;
    
    @ExistForTesting
    long count();
    
    @ExistForTesting
    User save(User user);
    
    @ExistForTesting
    void existOrFail(long... ids);
    
    @ExistForTesting
    UserIdProjection readIdByUsername(String username);
    
    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
