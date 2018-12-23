package manon.user.err;

/**
 * A friendship relation already exists.
 */
@SuppressWarnings("serial")
public class FriendshipExistsException extends AbstractFriendshipException {
    
    public FriendshipExistsException(String userIdFrom, String userIdTo) {
        super(userIdFrom, userIdTo);
    }
}
