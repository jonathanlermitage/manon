package manon.graphql.user;

import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import manon.service.user.UserSnapshotService;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("unused") // methods used by GraphQL
@Component
@RequiredArgsConstructor
public class UserResolver implements GraphQLResolver<User> {
    
    private final UserSnapshotService userSnapshotService;
    
    public List<UserSnapshot> getUserSnapshots(User user) {
        return UserSnapshot.from(userSnapshotService.findAllByUserId(user.getId()));
    }
}
