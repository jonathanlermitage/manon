package manon.user.service;

import manon.user.document.User;
import manon.user.document.UserIdProjection;
import manon.user.document.UserVersionProjection;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.form.UserUpdateForm;
import manon.user.model.RegistrationState;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    
    Mono<Long> count();
    
    Mono<User> save(User user);
    
    void existOrFail(String... ids) throws UserNotFoundException;
    
    User readOne(String id) throws UserNotFoundException;
    
    User readByUsername(String username) throws UserNotFoundException;
    
    Mono<User> findByUsername(String username);
    
    UserVersionProjection readVersionById(String id) throws UserNotFoundException;
    
    UserIdProjection readIdByUsername(String username) throws UserNotFoundException;
    
    /**
     * Update a user's data.
     * @param userId user id.
     * @param userUpdateForm user data.
     */
    Mono<Void> update(String userId, UserUpdateForm userUpdateForm);
    
    Flux<User> findAll(Pageable pageable);
    
    /**
     * Create a new user and its user.
     * @param user user data.
     * @return new user.
     */
    Mono<User> create(User user) throws UserExistsException;
    
    Mono<Void> encodeAndSetPassword(String id, String password);
    
    Mono<Void> setRegistrationState(String id, RegistrationState registrationState);
}
