package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.app.security.PasswordEncoderService;
import manon.user.document.User;
import manon.user.document.UserIdProjection;
import manon.user.document.UserVersionProjection;
import manon.user.err.UserExistsException;
import manon.user.err.UserNotFoundException;
import manon.user.form.UserUpdateForm;
import manon.user.model.RegistrationState;
import manon.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    
    @Override
    public Mono<Long> count() {
        return userRepository.count();
    }
    
    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void existOrFail(String... ids) throws UserNotFoundException {
        for (String id : ids) {
            readOne(id);
        }
    }
    
    @Override
    public User readOne(String id) throws UserNotFoundException {
        return userRepository.findById(id).blockOptional().orElseThrow(UserNotFoundException::new);
    }
    
    @Override
    public Mono<Void> update(String userId, UserUpdateForm userUpdateForm) {
        return userRepository.update(userId, userUpdateForm);
    }
    
    @Override
    public Flux<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable.getSort());
    }
    
    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User readByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).blockOptional().orElseThrow(UserNotFoundException::new);
    }
    
    @Override
    public UserVersionProjection readVersionById(String id) throws UserNotFoundException {
        return userRepository.findVersionById(id).blockOptional().orElseThrow(UserNotFoundException::new);
    }
    
    @Override
    public UserIdProjection readIdByUsername(String username) throws UserNotFoundException {
        return userRepository.findVersionByUsername(username).blockOptional().orElseThrow(UserNotFoundException::new);
    }
    
    @Override
    public Mono<User> create(User user) throws UserExistsException {
        if (userRepository.countByUsername(user.getUsername()).block() > 0) {
            throw new UserExistsException();
        }
        return userRepository.save(user.toBuilder()
                .password(passwordEncoderService.encode(user.getPassword()))
                .build());
    }
    
    @Override
    public Mono<Void> encodeAndSetPassword(String id, String password) {
        return userRepository.setPassword(id, passwordEncoderService.encode(password));
    }
    
    @Override
    public Mono<Void> setRegistrationState(String id, RegistrationState registrationState) {
        return userRepository.setRegistrationState(id, registrationState);
    }
}
