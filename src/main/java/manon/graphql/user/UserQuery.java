package manon.graphql.user;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import manon.err.user.UserNotFoundException;
import manon.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused") // methods used by GraphQL
@Component
@RequiredArgsConstructor
public class UserQuery implements GraphQLQueryResolver {
    
    private final UserService userService;
    
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    public User getMyUser(DataFetchingEnvironment dfe) throws UserNotFoundException {
        long currentUserId = userService.readCurrentUser().getId();
        return User.from(userService.readOne(currentUserId));
    }
    
    @PreAuthorize("hasRole('ROLE_DEV')")
    public User getUserById(long id, DataFetchingEnvironment dfe) throws UserNotFoundException {
        return User.from(userService.readOne(id));
    }
}
