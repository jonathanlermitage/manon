package manon.user.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship request doesn't exist.
 */
@AllArgsConstructor
@Getter
public class FriendshipRequestNotFoundException extends Exception {
    
    private String userIdFrom;
    private String userIdTo;
}
