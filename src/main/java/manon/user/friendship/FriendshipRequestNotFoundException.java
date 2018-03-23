package manon.user.friendship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A friendship request doesn't exist.
 */
@RequiredArgsConstructor
@Getter
public class FriendshipRequestNotFoundException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
