package manon.service.user;

import manon.document.user.FriendshipEventEntity;
import manon.model.user.FriendshipEventCode;
import manon.util.ExistForTesting;

import java.util.List;

public interface FriendshipEventService {

    List<FriendshipEventEntity> findAllFriendshipEventsByUserOrderByCreationDateDesc(long userId);

    void registerEvents(long userIdFrom, long userIdTo, FriendshipEventCode eventCodeFrom, FriendshipEventCode eventCodeTo);

    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    long countAllFriendshipEventsByUser(long userId);

    @ExistForTesting(why = "AbstractIntegrationTest")
    void deleteAll();
}
