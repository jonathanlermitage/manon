package manon.user.err;

/**
 * A friendship request doesn't exist.
 */
@SuppressWarnings("serial")
public class FriendshipRequestNotFoundException extends AbstractFriendshipException {
    
    public FriendshipRequestNotFoundException(String userIdFrom, String userIdTo) {
        super(userIdFrom, userIdTo);
    }
}
