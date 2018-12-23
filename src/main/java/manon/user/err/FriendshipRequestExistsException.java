package manon.user.err;

/**
 * A friendship request already exists.
 */
@SuppressWarnings("serial")
public class FriendshipRequestExistsException extends AbstractFriendshipException {
    
    public FriendshipRequestExistsException(String userIdFrom, String userIdTo) {
        super(userIdFrom, userIdTo);
    }
}
