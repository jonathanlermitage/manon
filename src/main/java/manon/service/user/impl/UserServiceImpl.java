package manon.service.user.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import manon.document.user.QUser;
import manon.document.user.User;
import manon.document.user.UserIdProjection;
import manon.document.user.UserVersionProjection;
import manon.dto.user.UserWithSnapshotsResponseDto;
import manon.err.user.PasswordNotMatchException;
import manon.err.user.UserExistsException;
import manon.err.user.UserNotFoundException;
import manon.mapper.user.UserMapper;
import manon.model.user.RegistrationState;
import manon.model.user.form.UserUpdateForm;
import manon.repository.user.UserRepository;
import manon.service.user.PasswordEncoderService;
import manon.service.user.UserService;
import manon.util.ExistForTesting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    @Override
    public User readOne(long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserWithSnapshotsResponseDto readOneAndFetchUserSnapshotDtos(long id) {
        return UserMapper.MAPPER.userToUserWithSnapshotsResponseDto(
            userRepository.findAndFetchUserSnapshots(id).orElseThrow(UserNotFoundException::new));
    }

    @Override
    public void update(long userId, UserUpdateForm userUpdateForm) {
        userRepository.update(userId, userUpdateForm.getEmail(), userUpdateForm.getNickname());
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
    public User readByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserVersionProjection readVersionById(long id) {
        return userRepository.findVersionById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserExistsException();
        }
        return userRepository.save(user.toBuilder()
            .password(passwordEncoderService.encode(user.getPassword()))
            .build());
    }

    @Override
    public void encodeAndSetPassword(long id, String password) {
        userRepository.setPassword(id, passwordEncoderService.encode(password));
    }

    @Override
    public void setRegistrationState(long id, RegistrationState registrationState) {
        userRepository.setRegistrationState(id, registrationState);
    }

    @Override
    public void validatePassword(String rawPassword, String encodedPassword) throws PasswordNotMatchException {
        if (!passwordEncoderService.matches(rawPassword, encodedPassword)) {
            throw new PasswordNotMatchException();
        }
    }

    @Override
    public Page<User> search(Predicate predicate, Pageable pageable) {
        if (predicate == null) {
            return findAll(pageable);
        } else {
            return userRepository.findAll(predicate, pageable);
        }
    }

    @Override
    public Page<User> searchByIdentity(String username, String nickname, String email, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!StringUtils.isEmpty(username)) {
            predicate.or(QUser.user.username.equalsIgnoreCase(username));
        }
        if (!StringUtils.isEmpty(nickname)) {
            predicate.or(QUser.user.nickname.equalsIgnoreCase(nickname));
        }
        if (!StringUtils.isEmpty(email)) {
            predicate.or(QUser.user.email.equalsIgnoreCase(email));
        }
        return search(predicate.getValue(), pageable);
    }

    @Override
    @ExistForTesting
    public User readOneAndFetchUserSnapshots(long id) {
        return userRepository.findAndFetchUserSnapshots(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    @ExistForTesting
    public long count() {
        return userRepository.count();
    }

    @Override
    @ExistForTesting
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @ExistForTesting
    public void existOrFail(long... ids) {
        for (long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new UserNotFoundException();
            }
        }
    }

    @Override
    @ExistForTesting
    public UserIdProjection readIdByUsername(String username) {
        return userRepository.findVersionByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
