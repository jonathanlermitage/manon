package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.Friendship;
import manon.document.user.FriendshipRequest;
import manon.document.user.User;
import manon.err.user.FriendshipExistsException;
import manon.err.user.FriendshipRequestExistsException;
import manon.err.user.FriendshipRequestNotFoundException;
import manon.model.user.FriendshipEventCode;
import manon.repository.user.FriendshipRequestRepository;
import manon.service.user.FriendshipEventService;
import manon.service.user.FriendshipRequestService;
import manon.service.user.FriendshipService;
import manon.service.user.UserService;
import manon.util.ExistForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static manon.model.user.FriendshipEventCode.TARGET_ACCEPTED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.TARGET_CANCELED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.TARGET_REJECTED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.TARGET_SENT_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.YOU_ACCEPTED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.YOU_CANCELED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.YOU_REJECTED_FRIEND_REQUEST;
import static manon.model.user.FriendshipEventCode.YOU_SENT_FRIEND_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipRequestServiceImpl implements FriendshipRequestService {
    
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final FriendshipService friendshipService;
    private final FriendshipEventService friendshipEventService;
    private final UserService userService;
    
    @Override
    public void askFriendship(long userIdFrom, long userIdTo) {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            throw new FriendshipRequestExistsException();
        }
        if (friendshipService.countCouple(userIdFrom, userIdTo) > 0) {
            throw new FriendshipExistsException();
        }
        
        User userFrom = userService.readOne(userIdFrom);
        User userTo = userService.readOne(userIdTo);
        friendshipRequestRepository.save(FriendshipRequest.builder()
            .requestFrom(userFrom)
            .requestTo(userTo)
            .build());
        
        friendshipEventService.registerEvents(userIdFrom, userIdTo, YOU_SENT_FRIEND_REQUEST, TARGET_SENT_FRIEND_REQUEST);
    }
    
    @Override
    public void acceptFriendshipRequest(long userIdFrom, long userIdTo) {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRequestRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipRequestNotFoundException();
        }
        
        User userFrom = userService.readOne(userIdFrom);
        User userTo = userService.readOne(userIdTo);
        friendshipService.save(Friendship.builder()
            .requestFrom(userFrom)
            .requestTo(userTo)
            .build());
        
        friendshipEventService.registerEvents(userIdFrom, userIdTo, TARGET_ACCEPTED_FRIEND_REQUEST, YOU_ACCEPTED_FRIEND_REQUEST);
    }
    
    @Override
    public void rejectFriendshipRequest(long userIdFrom, long userIdTo) {
        deleteFriendshipRequest(userIdFrom, userIdTo, TARGET_REJECTED_FRIEND_REQUEST, YOU_REJECTED_FRIEND_REQUEST);
    }
    
    @Override
    public void cancelFriendshipRequest(long userIdFrom, long userIdTo) {
        deleteFriendshipRequest(userIdFrom, userIdTo, YOU_CANCELED_FRIEND_REQUEST, TARGET_CANCELED_FRIEND_REQUEST);
    }
    
    private void deleteFriendshipRequest(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo) {
        if (friendshipRequestRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRequestRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipRequestNotFoundException();
        }
        friendshipEventService.registerEvents(userIdFrom, userIdTo, eventCodeFrom, eventCodeTo);
    }
    
    @Override
    @ExistForTesting(why = "AbstractIntegrationTest")
    public void deleteAll() {
        friendshipRequestRepository.deleteAll();
    }
    
    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public long countFriendshipRequestCouple(long userId1, long userId2) {
        return friendshipRequestRepository.countCouple(userId1, userId2);
    }
    
    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public List<FriendshipRequest> findAllFriendshipRequestsByRequestFrom(long userId) {
        return friendshipRequestRepository.findAllByRequestFrom(userId);
    }
    
    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public List<FriendshipRequest> findAllFriendshipRequestsByRequestTo(long userId) {
        return friendshipRequestRepository.findAllByRequestTo(userId);
    }
}
