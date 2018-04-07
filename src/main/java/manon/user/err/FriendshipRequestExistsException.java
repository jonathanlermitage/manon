package manon.user.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship request already exists.
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class FriendshipRequestExistsException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
