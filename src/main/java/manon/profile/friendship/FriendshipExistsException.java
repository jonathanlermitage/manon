package manon.profile.friendship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A friendship relation already exists.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendshipExistsException extends Exception {
    
    private String profileIdFrom;
    private String profileIdTo;
}
