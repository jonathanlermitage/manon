package manon.user.friendship.service;

import lombok.RequiredArgsConstructor;
import manon.user.UserNotFoundException;
import manon.user.document.User;
import manon.user.friendship.FriendshipExistsException;
import manon.user.friendship.FriendshipRequestExistsException;
import manon.user.friendship.FriendshipRequestNotFoundException;
import manon.user.repository.UserRepository;
import manon.user.service.UserService;
import org.springframework.stereotype.Service;

import static manon.user.document.User.Validation.MAX_EVENTS;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {
    
    private final UserRepository userRepository;
    private final UserService userService;
    
    @Override
    public void keepEvents(String... ids) throws UserNotFoundException {
        for (String id : ids) {
            userRepository.keepEvents(id, MAX_EVENTS);
        }
    }
    
    @Override
    public void askFriendship(String userIdFrom, String userIdTo)
            throws UserNotFoundException, FriendshipExistsException, FriendshipRequestExistsException {
        User from = userService.readOne(userIdFrom);
        if (from.getFriends().contains(userIdTo)) {
            throw new FriendshipExistsException(userIdFrom, userIdTo);
        }
        if (from.getFriendshipRequestsFrom().contains(userIdTo)) {
            throw new FriendshipRequestExistsException(userIdTo, userIdFrom);
        }
        if (from.getFriendshipRequestsTo().contains(userIdTo)) {
            throw new FriendshipRequestExistsException(userIdFrom, userIdTo);
        }
        userRepository.askFriendship(userIdFrom, userIdTo);
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void acceptFriendshipRequest(String userIdFrom, String userIdTo)
            throws UserNotFoundException, FriendshipRequestNotFoundException {
        if (!userService.readOne(userIdTo).getFriendshipRequestsFrom().contains(userIdFrom)) {
            throw new FriendshipRequestNotFoundException(userIdFrom, userIdTo);
        }
        userRepository.acceptFriendshipRequest(userIdFrom, userIdTo);
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void rejectFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException {
        userRepository.rejectFriendshipRequest(userIdFrom, userIdTo);
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void cancelFriendshipRequest(String userIdFrom, String userIdTo) throws UserNotFoundException {
        userRepository.cancelFriendshipRequest(userIdFrom, userIdTo);
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void revokeFriendship(String userIdFrom, String userIdTo) throws UserNotFoundException {
        userRepository.revokeFriendship(userIdFrom, userIdTo);
        keepEvents(userIdFrom, userIdTo);
    }
}
