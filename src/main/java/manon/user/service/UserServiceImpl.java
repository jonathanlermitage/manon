package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.app.security.PasswordEncoderService;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.document.UserVersion;
import manon.user.form.UserUpdateForm;
import manon.user.registration.RegistrationState;
import manon.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    
    @Override
    public long count() {
        return userRepository.count();
    }
    
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
    
    @Override
    public void existOrFail(String... ids) throws UserNotFoundException {
        for (String id : ids) {
            readOne(id);
        }
    }
    
    @Override
    public User readOne(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @Override
    public void update(String userId, UserUpdateForm userUpdateForm) {
        userRepository.update(userId, userUpdateForm);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User readByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
    
    @Override
    public UserVersion readVersionById(String id) throws UserNotFoundException {
        return userRepository.findVersionById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @Override
    public User create(User user) throws UserExistsException {
        if (userRepository.usernameExists(user.getUsername())) {
            throw new UserExistsException(user.getUsername());
        }
        return userRepository.save(user.toBuilder()
                .password(passwordEncoderService.encode(user.getPassword()))
                .build());
    }
    
    @Override
    public void encodeAndSetPassword(String id, String password) {
        userRepository.setPassword(id, passwordEncoderService.encode(password));
    }
    
    @Override
    public void setRegistrationState(String id, RegistrationState registrationState) {
        userRepository.setRegistrationState(id, registrationState);
    }
}
