package manon.user.err;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship action error.
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public abstract class AbstractFriendshipException extends Exception {
    
    private final String userIdFrom;
    private final String userIdTo;
}
