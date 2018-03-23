package manon.user.friendship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A friendship relation already exists.
 */
@RequiredArgsConstructor
@Getter
public class FriendshipExistsException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
