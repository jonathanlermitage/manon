package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.profile.service.ProfileService;
import manon.user.UserExistsException;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.registration.RegistrationStateEnum;
import manon.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("UserService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    
    private static final String CACHE_READ_USER_BY_ID = "READ_USER_BY_ID";
    private final UserRepository userRepository;
    private final ProfileService profileService;
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User readByUsername(String username)
            throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return user.get();
    }
    
    @Cacheable(value = CACHE_READ_USER_BY_ID, key = "#id")
    @Override
    public User readOne(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @Caching(put = @CachePut(value = CACHE_READ_USER_BY_ID, key = "#result.id"))
    @Override
    public User create(User user)
            throws UserExistsException {
        if (userRepository.usernameExists(user.getUsername())) {
            throw new UserExistsException(user.getUsername());
        }
        String profileId = profileService.create().getId();
        user = user.toBuilder()
                .profileId(profileId)
                .build();
        userRepository.save(user);
        return user;
    }
    
    @CacheEvict(value = CACHE_READ_USER_BY_ID, key = "#id")
    @Override
    public void setPassword(String id, String password) throws UserNotFoundException {
        userRepository.setPassword(id, password);
    }
    
    @CacheEvict(value = CACHE_READ_USER_BY_ID, key = "#id")
    @Override
    public void setRegistrationState(String id, RegistrationStateEnum registrationState) throws UserNotFoundException {
        userRepository.setRegistrationState(id, registrationState);
    }
}
