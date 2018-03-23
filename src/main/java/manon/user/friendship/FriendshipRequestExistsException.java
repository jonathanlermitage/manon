package manon.user.friendship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A friendship request already exists.
 */
@RequiredArgsConstructor
@Getter
public class FriendshipRequestExistsException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
