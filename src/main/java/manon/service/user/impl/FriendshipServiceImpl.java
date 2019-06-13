package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.Friendship;
import manon.err.user.FriendshipNotFoundException;
import manon.model.user.FriendshipEventCode;
import manon.model.user.UserPublicInfo;
import manon.repository.user.FriendshipRepository;
import manon.service.user.FriendshipEventService;
import manon.service.user.FriendshipService;
import manon.util.ExistForTesting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static manon.model.user.FriendshipEventCode.TARGET_REVOKED_FRIENDSHIP;
import static manon.model.user.FriendshipEventCode.YOU_REVOKED_FRIENDSHIP;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipServiceImpl implements FriendshipService {
    
    private final FriendshipRepository friendshipRepository;
    private final FriendshipEventService friendshipEventService;
    
    @Override
    public void revokeFriendship(long userIdFrom, long userIdTo) {
        deleteFriendship(userIdFrom, userIdTo, YOU_REVOKED_FRIENDSHIP, TARGET_REVOKED_FRIENDSHIP);
    }
    
    @SuppressWarnings("SameParameterValue")
    private void deleteFriendship(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo) {
        if (friendshipRepository.countCouple(userIdFrom, userIdTo) > 0) {
            friendshipRepository.deleteCouple(userIdFrom, userIdTo);
        } else {
            throw new FriendshipNotFoundException();
        }
        friendshipEventService.registerEvents(userIdFrom, userIdTo, eventCodeFrom, eventCodeTo);
    }
    
    @Override
    public List<UserPublicInfo> findAllPublicInfoFor(long userId) {
        try (Stream<Friendship> stream = friendshipRepository.streamAllFor(userId)) {
            return stream.map(friendship -> UserPublicInfo.from(friendship.getRequestFrom().getId() == userId ? friendship.getRequestTo() : friendship.getRequestFrom()))
                .collect(Collectors.toList());
        }
    }
    
    @Override
    public long countCouple(long userId1, long userId2) {
        return friendshipRepository.countCouple(userId1, userId2);
    }
    
    @Override
    public Friendship save(Friendship entity) {
        return friendshipRepository.save(entity);
    }
    
    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public void deleteAll() {
        friendshipRepository.deleteAll();
    }
    
    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public List<Friendship> findAllFor(long userId) {
        return friendshipRepository.findAllFor(userId);
    }
}
