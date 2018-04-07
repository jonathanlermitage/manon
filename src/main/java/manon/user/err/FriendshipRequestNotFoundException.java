package manon.user.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship request doesn't exist.
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class FriendshipRequestNotFoundException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
