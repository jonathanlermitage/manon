package manon.service.user;

import com.querydsl.core.types.Predicate;
import manon.document.user.UserEntity;
import manon.document.user.UserIdProjection;
import manon.document.user.UserVersionProjection;
import manon.dto.user.UserWithSnapshotsDto;
import manon.err.user.PasswordNotMatchException;
import manon.model.user.RegistrationState;
import manon.model.user.form.UserUpdateForm;
import manon.util.ExistForTesting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserEntity readOne(long id);

    UserWithSnapshotsDto readOneAndFetchUserSnapshotDtos(long id);

    UserEntity readByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    UserVersionProjection readVersionById(long id);

    /**
     * Update a user's data.
     * @param userId user id.
     * @param userUpdateForm user data.
     */
    void update(long userId, UserUpdateForm userUpdateForm);

    Page<UserEntity> findAll(Pageable pageable);

    /**
     * Create a new user and its user.
     * @param user user data.
     * @return new user.
     */
    UserEntity create(UserEntity user);

    void encodeAndSetPassword(long id, String password);

    void setRegistrationState(long id, RegistrationState registrationState);

    /**
     * Check if a raw password validates an encoded password, and throw an exception if validation failed.
     * @param rawPassword raw password.
     * @param encodedPassword encoded password.
     */
    void validatePassword(String rawPassword, String encodedPassword) throws PasswordNotMatchException;

    /**
     * Use Querydsl to search users.
     * @param predicate query.
     * @param pageable pagination.
     * @return users.
     */
    Page<UserEntity> search(Predicate predicate, Pageable pageable);

    /**
     * Use Querydsl to search users by the union of some filters: username, nickname or email.
     * @param username username filter.
     * @param nickname nickname filter.
     * @param email email filter.
     * @param pageable pagination.
     * @return users.
     */
    Page<UserEntity> searchByIdentity(String username, String nickname, String email, Pageable pageable);

    @ExistForTesting
    UserEntity readOneAndFetchUserSnapshots(long id);

    @ExistForTesting
    long count();

    @ExistForTesting
    UserEntity save(UserEntity user);

    @ExistForTesting
    void existOrFail(long... ids);

    @ExistForTesting
    UserIdProjection readIdByUsername(String username);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
