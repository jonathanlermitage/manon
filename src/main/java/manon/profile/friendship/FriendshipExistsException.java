package manon.profile.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A friendship relation already exists.
 */
@AllArgsConstructor
@Getter
public class FriendshipExistsException extends Exception {
    
    private String profileIdFrom;
    private String profileIdTo;
}
