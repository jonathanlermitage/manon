package manon.user.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship request already exists.
 */
@AllArgsConstructor
@Getter
public class FriendshipRequestExistsException extends Exception {
    
    private String userIdFrom;
    private String userIdTo;
}
