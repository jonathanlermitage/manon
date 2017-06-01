package manon.profile.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A friendship request already exists.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendshipRequestExistsException extends Exception {
    
    private String profileIdFrom;
    private String profileIdTo;
}
