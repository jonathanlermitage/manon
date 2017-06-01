package manon.profile.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A friendship request doesn't exist.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendshipRequestNotFoundException extends Exception {
    
    private String profileIdFrom;
    private String profileIdTo;
}
