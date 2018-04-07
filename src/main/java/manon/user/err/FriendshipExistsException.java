package manon.user.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship relation already exists.
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class FriendshipExistsException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
