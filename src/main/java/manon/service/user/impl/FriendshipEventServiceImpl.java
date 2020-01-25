package manon.service.user.impl;

import lombok.RequiredArgsConstructor;
import manon.document.user.FriendshipEventEntity;
import manon.document.user.UserEntity;
import manon.model.user.FriendshipEventCode;
import manon.repository.user.FriendshipEventRepository;
import manon.service.user.FriendshipEventService;
import manon.service.user.UserService;
import manon.util.ExistForTesting;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipEventServiceImpl implements FriendshipEventService {

    private final FriendshipEventRepository friendshipEventRepository;
    private final UserService userService;

    @Override
    public List<FriendshipEventEntity> findAllFriendshipEventsByUserOrderByCreationDateDesc(long userId) {
        return friendshipEventRepository.findAllByUserOrderByCreationDateDesc(userId);
    }

    public void registerEvents(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo) {
        UserEntity userFrom = userService.readOne(userIdFrom);
        UserEntity userTo = userService.readOne(userIdTo);
        friendshipEventRepository.saveAll(Arrays.asList(
            FriendshipEventEntity.builder()
                .user(userFrom)
                .friend(userTo)
                .code(eventCodeFrom)
                .build(),
            FriendshipEventEntity.builder()
                .user(userTo)
                .friend(userFrom)
                .code(eventCodeTo)
                .build()
        ));
        keepEvents(userIdFrom, userIdTo);
    }

    /** Keep only {@link FriendshipEventEntity.Validation#MAX_EVENTS_PER_USER} recent friendshipEvents on users. */
    private void keepEvents(@NotNull long... userIds) {
        for (long userId : userIds) {
            List<FriendshipEventEntity> events = friendshipEventRepository.findAllByUserOrderByCreationDateDesc(userId);
            if (events.size() > FriendshipEventEntity.Validation.MAX_EVENTS_PER_USER) {
                friendshipEventRepository.deleteAll(events.subList(FriendshipEventEntity.Validation.MAX_EVENTS_PER_USER, events.size()));
            }
        }
    }

    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public long countAllFriendshipEventsByUser(long userId) {
        return friendshipEventRepository.countAllByUser(userId);
    }

    @Override
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    public void deleteAll() {
        friendshipEventRepository.deleteAll();
    }
}
