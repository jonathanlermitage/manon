package manon.graphql.user;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import manon.err.user.UserNotFoundException;
import manon.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused") // methods used by GraphQL
@Component
@RequiredArgsConstructor
public class UserMutation implements GraphQLMutationResolver {
    
    private final UserService userService;
    
    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public User setMyNickname(String nickname) throws UserNotFoundException {
        manon.document.user.User originalUser = userService.readCurrentUser();
        manon.document.user.User futureUser = originalUser.toBuilder().nickname(nickname).build();
        
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<manon.document.user.User>> violations = validator.validate(futureUser);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("model validation failed: " +
                violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(",")));
        }
        
        return User.from(userService.save(futureUser));
    }
}
