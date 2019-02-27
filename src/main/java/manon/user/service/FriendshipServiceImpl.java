package manon.user.service;

import lombok.RequiredArgsConstructor;
import manon.user.document.Friendship;
import manon.user.document.FriendshipEvent;
import manon.user.document.FriendshipRequest;
import manon.user.document.User;
import manon.user.err.FriendshipExistsException;
import manon.user.err.FriendshipNotFoundException;
import manon.user.err.FriendshipRequestExistsException;
import manon.user.err.FriendshipRequestNotFoundException;
import manon.user.err.UserNotFoundException;
import manon.user.model.FriendshipEventCode;
import manon.user.model.UserPublicInfo;
import manon.user.repository.FriendshipEventRepository;
import manon.user.repository.FriendshipRepository;
import manon.user.repository.FriendshipRequestRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static manon.user.model.FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.TARGET_REVOKED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_REVOKED_FRIEND_REQUEST;
import static manon.user.model.FriendshipEventCode.YOU_SENT_FRIEND_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipServiceImpl implements FriendshipService {
    
    private final FriendshipEventRepository friendshipEventRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserService userService;
    
    /** Keep only {@link FriendshipEvent.Validation#MAX_EVENTS_PER_USER} recent friendshipEvents on users. */
    private void keepEvents(@NotNull long... userIds) {
        for (long userId : userIds) {
            List<FriendshipEvent> events = friendshipEventRepository.findAllByUserOrderByCreationDateDesc(userId);
            if (events.size() > FriendshipEvent.Validation.MAX_EVENTS_PER_USER) {
                friendshipEventRepository.deleteAll(events.subList(FriendshipEvent.Validation.MAX_EVENTS_PER_USER, events.size()));
            }
        }
    }
    
    @Override
    public void askFriendship(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipExistsException, FriendshipRequestExistsException {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            throw new FriendshipRequestExistsException();
        }
        if (friendshipRepository.countCouple(userIdFrom, userIdTo) > 0) {
            throw new FriendshipExistsException();
        }
        
        User userFrom = userService.readOne(userIdFrom);
        User userTo = userService.readOne(userIdTo);
        friendshipRequestRepository.save(FriendshipRequest.builder()
            .requestFrom(userFrom)
            .requestTo(userTo)
            .build());
        
        registerEvents(userIdFrom, userIdTo, YOU_SENT_FRIEND_REQUEST, TARGET_SENT_FRIEND_REQUEST);
    }
    
    @Override
    public void acceptFriendshipRequest(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRequestRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipRequestNotFoundException();
        }
        
        User userFrom = userService.readOne(userIdFrom);
        User userTo = userService.readOne(userIdTo);
        friendshipRepository.save(Friendship.builder()
            .requestFrom(userFrom)
            .requestTo(userTo)
            .build());
        
        registerEvents(userIdFrom, userIdTo, TARGET_ACCEPTED_FRIEND_REQUEST, YOU_ACCEPTED_FRIEND_REQUEST);
    }
    
    @Override
    public void rejectFriendshipRequest(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        deleteFriendshipRequest(userIdFrom, userIdTo, TARGET_REJECTED_FRIEND_REQUEST, YOU_REJECTED_FRIEND_REQUEST);
    }
    
    @Override
    public void cancelFriendshipRequest(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        deleteFriendshipRequest(userIdFrom, userIdTo, YOU_CANCELED_FRIEND_REQUEST, TARGET_CANCELED_FRIEND_REQUEST);
    }
    
    @Override
    public void revokeFriendship(long userIdFrom, long userIdTo)
        throws UserNotFoundException, FriendshipNotFoundException {
        deleteFriendship(userIdFrom, userIdTo, YOU_REVOKED_FRIEND_REQUEST, TARGET_REVOKED_FRIEND_REQUEST);
    }
    
    private void registerEvents(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo)
        throws UserNotFoundException {
        User userFrom = userService.readOne(userIdFrom);
        User userTo = userService.readOne(userIdTo);
        friendshipEventRepository.saveAll(Arrays.asList(
            FriendshipEvent.builder()
                .user(userFrom)
                .friend(userTo)
                .code(eventCodeFrom)
                .build(),
            FriendshipEvent.builder()
                .user(userTo)
                .friend(userFrom)
                .code(eventCodeTo)
                .build()
        ));
        keepEvents(userIdFrom, userIdTo);
    }
    
    @SuppressWarnings("SameParameterValue")
    private void deleteFriendship(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo)
        throws UserNotFoundException, FriendshipNotFoundException {
        if (friendshipRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipNotFoundException();
        }
        registerEvents(userIdFrom, userIdTo, eventCodeFrom, eventCodeTo);
    }
    
    private void deleteFriendshipRequest(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo)
        throws UserNotFoundException, FriendshipRequestNotFoundException {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRequestRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipRequestNotFoundException();
        }
        registerEvents(userIdFrom, userIdTo, eventCodeFrom, eventCodeTo);
    }
    
    @Override
    public List<UserPublicInfo> findAllFor(long userId) {
        try (Stream<Friendship> stream = friendshipRepository.streamAllFor(userId)) {
            return stream.map(friendship -> UserPublicInfo.from(friendship.getRequestFrom().getId() == userId ? friendship.getRequestTo() : friendship.getRequestFrom()))
                .collect(Collectors.toList());
        }
    }
}
