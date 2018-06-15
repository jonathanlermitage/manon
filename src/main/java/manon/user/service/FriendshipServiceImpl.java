package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.User;
import manon.user.err.FriendshipExistsException;
import manon.user.err.FriendshipRequestExistsException;
import manon.user.err.FriendshipRequestNotFoundException;
import manon.user.err.UserNotFoundException;
import manon.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

import static manon.user.document.User.Validation.MAX_EVENTS;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {
    
    private final UserRepository userRepository;
    private final UserService userService;
    
    @Override
    public void keepEvents(String... ids) {
        Set<Mono<Void>> monos = new HashSet<>();
        for (String id : ids) {
            monos.add(userRepository.keepEvents(id, MAX_EVENTS));
        }
        Flux.concat(monos).all(aVoid -> true).block();
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
        userRepository.askFriendship(userIdFrom, userIdTo).block();
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void acceptFriendshipRequest(String userIdFrom, String userIdTo)
            throws UserNotFoundException, FriendshipRequestNotFoundException {
        if (!userService.readOne(userIdTo).getFriendshipRequestsFrom().contains(userIdFrom)) {
            throw new FriendshipRequestNotFoundException(userIdFrom, userIdTo);
        }
        userRepository.acceptFriendshipRequest(userIdFrom, userIdTo).block();
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void rejectFriendshipRequest(String userIdFrom, String userIdTo) {
        userRepository.rejectFriendshipRequest(userIdFrom, userIdTo).block();
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void cancelFriendshipRequest(String userIdFrom, String userIdTo) {
        userRepository.cancelFriendshipRequest(userIdFrom, userIdTo).block();
        keepEvents(userIdFrom, userIdTo);
    }
    
    @Override
    public void revokeFriendship(String userIdFrom, String userIdTo) {
        userRepository.revokeFriendship(userIdFrom, userIdTo).block();
        keepEvents(userIdFrom, userIdTo);
    }
}
